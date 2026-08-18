/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xtiyo
 */
public class ConectorSqlite {

    private Connection conexion;
    private String error = null;

    public ConectorSqlite(String user, String pass, String bd, String host) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conexion = DriverManager.getConnection("jdbc:sqlite:" + bd, user, pass);
            System.out.println("[SQLite] Conexion abierta con exito a jdbc:sqlite:" + bd);
        } catch (ClassNotFoundException | SQLException ex) {
            this.error = ex.getMessage();
            System.err.println("[SQLite] Error al abrir conexion: " + this.error);
        }
    }

    /**
     * Abre la conexión directamente con una URL JDBC completa
     * (p.ej. {@code jdbc:sqlite:db/boletas.db}). Se usa para compartir
     * exactamente la misma base que lee el demonio de sincronización.
     */
    public ConectorSqlite(String jdbcUrl) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conexion = DriverManager.getConnection(jdbcUrl);
            System.out.println("[SQLite] Conexion abierta con exito a " + jdbcUrl);
        } catch (ClassNotFoundException | SQLException ex) {
            this.error = ex.getMessage();
            System.err.println("[SQLite] Error al abrir conexion a " + jdbcUrl + ": " + this.error);
        }
    }

    public String getError() {
        return this.error;
    }

    public Connection getConexion() {
        return this.conexion;
    }

    public void Cerrar() {
        try {
            this.conexion.close();
            System.out.println("[SQLite] Conexion cerrada correctamente.");
        } catch (SQLException ex) {
            Logger.getLogger(ConectorSqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Deja la base local vacía. Se invoca al inicio de cada generación: cada
     * tirada parte de cero y la base refleja siempre la última generada.
     */
    public void limpiarTabla() {
        if (this.conexion != null) {
            try {
                try (Statement stmt = this.conexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM boleta_local_sync");
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='boleta_local_sync'");
                    // Tabla de una version anterior que llevaba numeros consumidos
                    // entre generaciones; si existe, tambien se vacia.
                    stmt.executeUpdate("DROP TABLE IF EXISTS numero_consumido");
                    System.out.println("[SQLite] Base local vaciada: nueva generacion desde cero.");
                }
            } catch (SQLException e) {
                System.err.println("[SQLite] Error al limpiar tabla local: " + e.getMessage());
            }
        }
    }

    public boolean insertarSincronizacion(int idSorteo, String numeroBoleta, String numerosBloque, String qrToken) {
        String sql = "INSERT INTO boleta_local_sync(id_sorteo_nube, numero_boleta, numeros_oportunidades, qr_token) VALUES(?,?,?,?)";
        try (java.sql.PreparedStatement pstmt = this.conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idSorteo);
            pstmt.setString(2, numeroBoleta);
            pstmt.setString(3, numerosBloque);
            pstmt.setString(4, qrToken);
            int filas = pstmt.executeUpdate();
            System.out.println("[SQLite] Registro local insertado. idSorteo=" + idSorteo
                    + ", numero=" + numeroBoleta
                    + ", numeros_oportunidades="
                    + (numerosBloque.length() > 20 ? numerosBloque.substring(0, 20) + "..." : numerosBloque)
                    + ", filas=" + filas);
            return filas > 0;
        } catch (java.sql.SQLException e) {
            System.err.println(
                    "[SQLite] Error al insertar registro local. Numeros=" + numerosBloque + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea la tabla local si no existe. Es idempotente.
     */
    public boolean inicializarEsquema() {
        if (this.conexion == null) {
            return false;
        }
        try (Statement stmt = this.conexion.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS boleta_local_sync ("
                            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + " id_sorteo_nube INTEGER NOT NULL,"
                            + " numero_boleta TEXT NOT NULL,"
                            + " qr_token TEXT,"
                            + " fecha_generacion TEXT DEFAULT (datetime('now')),"
                            + " sync_status TEXT DEFAULT 'PENDIENTE_SYNC',"
                            + " fecha_sync TEXT,"
                            + " numeros_oportunidades TEXT)");
            return true;
        } catch (SQLException e) {
            this.error = e.getMessage();
            System.err.println("[SQLite] Error al crear el esquema local: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra el lote de boletas emitidas en boleta_local_sync (una sola
     * transacción). Es lo que alimenta al demonio de sincronización con Oracle.
     *
     * @param boletas cada fila: {numeroBoleta, numerosOportunidades, qrToken}
     * @return cantidad de filas insertadas, o -1 si falló.
     */
    public int registrarBoletas(int idSorteo, java.util.List<String[]> boletas) {
        if (this.conexion == null || boletas == null || boletas.isEmpty()) {
            return 0;
        }
        String sql = "INSERT INTO boleta_local_sync(id_sorteo_nube, numero_boleta, numeros_oportunidades, qr_token)"
                + " VALUES(?,?,?,?)";
        boolean autoCommitPrevio = true;
        try {
            autoCommitPrevio = this.conexion.getAutoCommit();
            this.conexion.setAutoCommit(false);
            int total = 0;
            try (PreparedStatement pstmt = this.conexion.prepareStatement(sql)) {
                for (String[] fila : boletas) {
                    pstmt.setInt(1, idSorteo);
                    pstmt.setString(2, fila[0]);
                    pstmt.setString(3, fila[1]);
                    pstmt.setString(4, fila[2]);
                    pstmt.addBatch();
                }
                for (int filas : pstmt.executeBatch()) {
                    if (filas > 0) {
                        total += filas;
                    }
                }
            }
            this.conexion.commit();
            System.out.println("[SQLite] Boletas registradas localmente: " + total + " (idSorteo=" + idSorteo + ")");
            return total;
        } catch (SQLException e) {
            this.error = e.getMessage();
            System.err.println("[SQLite] Error al registrar boletas: " + e.getMessage());
            try {
                this.conexion.rollback();
            } catch (SQLException ignore) {
            }
            return -1;
        } finally {
            try {
                this.conexion.setAutoCommit(autoCommitPrevio);
            } catch (SQLException ignore) {
            }
        }
    }
}
