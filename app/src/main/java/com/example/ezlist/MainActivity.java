package com.example.ezlist;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "grocery_store_data";
    private static final String URL = "jdbc:mysql://18.117.171.203:3306/"+DATABASE_NAME;
    private static final String USER = "android";
    private static final String PASSWORD = "android";
    public static final String TABLE_NAME = "grocery_store";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            utilFun();
            makeListDB();
            TableManipulator lm = new TableManipulator(DATABASE_NAME, URL, USER, PASSWORD, "user_grocery_list");
            lm.addItemToTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void utilFun() throws SQLException {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
                while (rs.next()) {
                    String columnValue = rs.getString("name");
                    Log.d("Database Result", columnValue);
                }

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Push working
        }).start();
    }

    public void makeListDB() throws SQLException {
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
    }


}