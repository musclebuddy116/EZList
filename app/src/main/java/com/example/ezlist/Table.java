package com.example.ezlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Table {
    //String DATABASE_NAME;
    //String URL;
    //String USER;
    //String PASSWORD;
    String TABLE_NAME;
    TableType tableType;
    /*public Table(String DATABASE_NAME, String URL, String USER, String PASSWORD, String TABLE_NAME) {
        this.DATABASE_NAME = DATABASE_NAME;
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.TABLE_NAME = TABLE_NAME;
        makeTable();
    }*/

    // Constructor to initialize table name and type, then call appropriate table creation method
    public Table(String TABLE_NAME, TableType type) {
        this.TABLE_NAME = TABLE_NAME;
        this.tableType = type;
        switch (tableType) {
            case GROCERY:
                makeGroceryTable();
                break;
            case PANTRY:
                makePantryTable();
                break;
            case RECIPE:
                makeRecipeTable();
                break;
        }
    }

    // Method to create the 'grocery' table
    void makeGroceryTable() {
        new Thread(() -> {
            try {
                // Load the MySQL driver and establish a connection
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();

                // Execute the SQL statement to create the table
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME + "(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "category VARCHAR (255)" +
                        ");");


                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // Method to create the 'pantry' table
    void makePantryTable() {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                // Load the MySQL driver and establish a connection
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();

                // Execute the SQL statement to create the table
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME + "(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL, " +
                        "category VARCHAR(255)," +
                        "shelf_life INT" +  // Add shelf_life column
                        ");");

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // Method to create the 'recipe' table (currently not implemented)
    void makeRecipeTable() {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                // Load the MySQL driver and establish a connection
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();

                // Execute the SQL statement to create the table (unfinished)
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME + "(" +
                        //FIXME: Insert table columns please
                        ");");

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
    void makeTable() {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME + "(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "category VARCHAR (255)" +
                        ");");

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

    }

    //Might not need this, not sure atm
    /*public void addItemToTable() throws SQLException {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();

                statement.executeUpdate("INSERT INTO " + TABLE_NAME + " (name, category)" +
                        //FIXME: Generalize insert values
                        "VALUES (\"Apple\", \"Fruit\");" );

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }*/
}


/*public void makeDB() throws SQLException {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();

                //FIXME: Change table name to match user
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                        //FIXME: Change to variable instead of string
                        "user_grocery_list" + "(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "category VARCHAR (255)" +
                        ");");

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }*/