package com.example.ezlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {
    // Table name for the pantry, will be modified to include the username
    public static String USER_TABLE_NAME = "pantry";

    private ListView itemsListView;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        itemsListView = findViewById(R.id.itemsListView);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        itemsListView.setAdapter(itemsAdapter);

        // Modify the table name to include the username
        USER_TABLE_NAME = Global.getUsername() + "_" + USER_TABLE_NAME;

        // Execute task to load pantry items from the databas
        new LoadItemsTask().execute();
    }

    // AsyncTask to load pantry items from the database
    private class LoadItemsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        // Background task to fetch pantry items from the database
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            new Table(USER_TABLE_NAME, TableType.PANTRY);
            ArrayList<String> itemList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM " + USER_TABLE_NAME);

                // Loop through the result set and process each item
                while (rs.next()) {
                    String expirationDate = rs.getString("expiration_date");
                    if (expirationDate == null || expirationDate.isEmpty()) {
                        expirationDate = "N/A";
                    }
                    // Add item name with expiration date to the list
                    String item = rs.getString("name") + " - Expires on: " + expirationDate;
                    itemList.add(item);
                }
                rs.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        // After background task completes, update the UI with the results
        @Override
        protected void onPostExecute(ArrayList<String> itemList) {
            // Check if any items were returned
            if (!itemList.isEmpty()) {
                itemsList.clear();
                itemsList.addAll(itemList);
                itemsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(PantryActivity.this, "No items found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
