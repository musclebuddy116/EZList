package com.example.ezlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://18.117.171.203:3306/grocery_store_data";
    private static final String USER = "android";
    private static final String PASSWORD = "android";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD); // Establishes the database connection
    }
}
