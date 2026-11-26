package com.mycompany.ahorropromax.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Patrón de utilidad para la conexión a MySQL.
 * Lee la configuración de /db.properties en el classpath.
 */
public final class ConexionDB {

    private static final String ARCHIVO_CONFIG = "/db.properties";
    private static final Properties PROPIEDADES = new Properties();
    private static String url;
    private static String usuario;
    private static String password;

    static {
        try (InputStream in = ConexionDB.class.getResourceAsStream(ARCHIVO_CONFIG)) {
            if (in == null) {
                throw new IllegalStateException(
                    "No se encontró " + ARCHIVO_CONFIG + " en el classpath");
            }
            PROPIEDADES.load(in);

            Class.forName(PROPIEDADES.getProperty("db.driver"));
            url      = PROPIEDADES.getProperty("db.url");
            usuario  = PROPIEDADES.getProperty("db.user");
            password = PROPIEDADES.getProperty("db.password");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                "Error inicializando ConexionDB: " + e.getMessage());
        }
    }

    private ConexionDB() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}