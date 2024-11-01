package com.example.ezlist;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://18.117.171.203:3306/grocery_store_data";
    private static final String USER = "android";
    private static final String PASSWORD = "android";

    // Establishes the database connection and logs success or failure
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Log.d("DatabaseHelper", "Database connection established successfully.");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Database connection failed. Check your URL, username, or password.", e);
        }
        return connection;
    }
}
