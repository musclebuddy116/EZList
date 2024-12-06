package com.example.ezlist;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper{

    // Establishes the database connection and logs success or failure
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
            Log.d("DatabaseHelper", "Database connection established successfully.");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Database connection failed. Check your URL, username, or password.", e);
        }
        return connection;
    }

    // Retrieves all items from the user's table
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM "  + Global.getUserTableName();

        // Execute the query and populate the list with item data
        try(Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet rs = preparedStatement.executeQuery()) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int shelf_life = rs.getInt("shelf_life");
                items.add(new Item(id, name, category, shelf_life));
            }
        }
        return items;
    }

    // Moves an item from the grocery table to the pantry table
    public void moveItemToPantry(String name, String category, int shelf_life) throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);

        try {
            // Remove the item from the current table
            String deleteQuery = "DELETE FROM " + Global.getUserTableName() + " WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            }

            // Insert the item into the pantry table
            Global.setUserTableName("_pantry_list");
            String insertQuery = "INSERT INTO " + Global.getUserTableName() + " (name, category, shelf_life) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,category);
                preparedStatement.setInt(3,shelf_life);
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }
}
