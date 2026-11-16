package com.ems.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Loads database settings from config/db.properties and hands out JDBC connections.
 */
public final class DBConnection {

    private static final String CONFIG_PATH = "config/db.properties";

    private static String url;
    private static String user;
    private static String password;

    private DBConnection() {
    }

    static {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_PATH)) {
            props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Could not read " + CONFIG_PATH + ". Copy config/db.properties.example to "
                            + CONFIG_PATH + " and fill in your MySQL credentials.");
        }
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password", "");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
