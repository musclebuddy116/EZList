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
import java.util.List;

public class ViewGroceryListActivity extends AppCompatActivity {
    public static String USER_TABLE_NAME_STEM = "_grocery_list";
//    public static String USER_TABLE_NAME_PANTRY = "_pantry_list";

    private DatabaseHelper dbHelper;
    private GroceryListAdapter adapter;

//    private ListView itemsListView;
//    private ArrayAdapter<String> itemsAdapter;
//    private ArrayList<String> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grocery_list);

        dbHelper = new DatabaseHelper();

        List<Item> items = null;
        try {
            Global.setUserTableName(USER_TABLE_NAME_STEM);
            items = dbHelper.getAllItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.grocery_list_view);
        adapter = new GroceryListAdapter(this, items, dbHelper);
        listView.setAdapter(adapter);
    }
}