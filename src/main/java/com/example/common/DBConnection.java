package com.example.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/concertdb";
    private static final String USER = "root";
    private static final String PASSWORD = "drowningBlueNile13!?";

    static {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try { resource.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}
