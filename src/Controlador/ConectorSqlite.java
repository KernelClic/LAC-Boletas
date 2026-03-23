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

    public void limpiarTabla() {
        if (this.conexion != null) {
            try {
                // Borrar todos los registros de la tabla de sincronización
                try (Statement stmt = this.conexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM boleta_local_sync");
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='boleta_local_sync'");
                    System.out.println("[SQLite] Tabla boleta_local_sync limpiada y secuencia reiniciada.");
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
}
