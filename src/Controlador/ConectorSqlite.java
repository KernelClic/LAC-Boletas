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

    public boolean insertarSincronizacion(int idSorteo, String numeroBoleta, String qrToken) {
        String sql = "INSERT INTO boleta_local_sync(id_sorteo_nube, numero_boleta, qr_token) VALUES(?,?,?)";
        try (java.sql.PreparedStatement pstmt = this.conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idSorteo);
            pstmt.setString(2, numeroBoleta);
            pstmt.setString(3, qrToken);
            int filas = pstmt.executeUpdate();
            System.out.println("[SQLite] Registro local insertado. idSorteo=" + idSorteo
                    + ", numero=" + numeroBoleta
                    + ", filas=" + filas);
            return filas > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("[SQLite] Error al insertar registro local numero=" + numeroBoleta + ": " + e.getMessage());
            return false;
        }
    }
}
