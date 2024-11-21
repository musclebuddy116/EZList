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
    public static String USER_TABLE_NAME_STEM = "_pantry_list";

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

        Global.setUserTableName(USER_TABLE_NAME_STEM);

        itemsAdapter = new ItemAdapter(this, itemsList, Global.getUserTableName());
        itemsListView.setAdapter(itemsAdapter);

        new LoadItemsTask().execute();
    }

    private class LoadItemsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            new Table(Global.getUserTableName(), TableType.PANTRY);
            ArrayList<String> itemList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT name, category FROM " + Global.getUserTableName());
                while (rs.next()) {
                    String name = rs.getString("name");
                    String category = rs.getString("category");

                    // Format the string as "Name (Category)"
                    String item = name + " (" + category + ")";
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
                Toast.makeText(PantryActivity.this, "No items found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}