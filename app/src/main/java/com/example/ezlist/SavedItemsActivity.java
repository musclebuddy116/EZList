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

public class SavedItemsActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "grocery_store_data";
    private static final String URL = "jdbc:mysql://18.117.171.203:3306/" + DATABASE_NAME;
    private static final String USER = "android";
    private static final String PASSWORD = "android";
    public static final String TABLE_NAME = "grocery_store";

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

        new LoadItemsTask().execute();
    }

    private class LoadItemsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> itemList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
                while (rs.next()) {
                    String expirationDate = rs.getString("expiration_date");
                    if (expirationDate == null || expirationDate.isEmpty()) {
                        expirationDate = "N/A";
                    }
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

        @Override
        protected void onPostExecute(ArrayList<String> itemList) {
            if (!itemList.isEmpty()) {
                itemsList.clear();
                itemsList.addAll(itemList);
                itemsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(SavedItemsActivity.this, "No items found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
