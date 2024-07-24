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
        } catch (ClassNotFoundException | SQLException ex) {
            this.error = ex.getMessage();
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
        } catch (SQLException ex) {
            Logger.getLogger(ConectorSqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
