package com.example.ezlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
public class GroceryActivity extends AppCompatActivity {

    public static final String TABLE_NAME = "grocery_store";
    public static String USER_TABLE_NAME = "_grocery_list";

    private EditText itemNameInput, daysBeforeInput;
    private Spinner categorySpinner;
    private Button addItemButton, viewItemsButton;
    private ListView searchResultsListView;
    private ArrayAdapter<String> searchResultsAdapter;
    private ArrayList<String> searchResultsList = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<CharSequence> unitAdapter;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        itemNameInput = findViewById(R.id.itemNameInput);
        searchResultsListView = findViewById(R.id.searchResultsListView);
        addItemButton = findViewById(R.id.addItemButton);
        viewItemsButton = findViewById(R.id.viewPantry);
        categorySpinner = findViewById(R.id.categorySpinner);

        categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        categorySpinner.setAdapter(categoryAdapter);


        searchResultsAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.listItemText, searchResultsList);
        searchResultsListView.setAdapter(searchResultsAdapter);

        USER_TABLE_NAME = Global.getUsername() + USER_TABLE_NAME;

        // Load categories from the database
        new GroceryActivity.LoadCategoriesTask().execute();

        // Add item button click listener
        addItemButton.setOnClickListener(v -> {
            String itemName = itemNameInput.getText().toString().trim();
            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            if (itemName.isEmpty()) {
                itemNameInput.setError("Item name is required");
                return;
            }

            // Add the item to the user's list
            new GroceryActivity.AddItemTask(selectedCategory, itemName).execute();

            itemNameInput.setText("");
        });

        viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroceryActivity.this, PantryActivity.class);
            startActivity(intent);
        });
        // Search functionality for item name input
        itemNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (searchText.isEmpty()) {
                    searchResultsListView.setVisibility(View.GONE);
                } else {
                    new SearchItemsTask(searchText).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Set the selected item in the input field when clicked from search results
        searchResultsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = searchResultsList.get(position);

            // Set the selected item in the itemNameInput field
            itemNameInput.setText(selectedItem);

            searchResultsListView.setVisibility(View.GONE);
        });
}
private class SearchItemsTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private String searchQuery;
        public SearchItemsTask(String searchQuery) {
            this.searchQuery = searchQuery;
        }
    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        ArrayList<String> itemsList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
            Statement statement = connection.createStatement();
            String query = "SELECT name FROM " + TABLE_NAME + " WHERE name LIKE '" + searchQuery + "%'";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                itemsList.add(rs.getString("name"));
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> itemsList) {
        // Display the filtered items only if there are multiple results
        if (itemsList.size() > 1) {
            displayFilteredItems(itemsList);
        } else if (itemsList.size() == 1) {
            // If there's only one result, set it directly to the input field
            itemNameInput.setText(itemsList.get(0));
            searchResultsListView.setVisibility(View.GONE);
        } else {
            searchResultsListView.setVisibility(View.GONE);
        }
    }
}

    private void displayFilteredItems(ArrayList<String> itemsList) {
        if (searchResultsAdapter != null) {
            searchResultsList.clear();
            searchResultsList.addAll(itemsList);
            searchResultsAdapter.notifyDataSetChanged();
            if (itemsList.isEmpty()) {
                searchResultsListView.setVisibility(View.GONE);
            } else {
                searchResultsListView.setVisibility(View.VISIBLE);
            }
        }
    }
    private class LoadCategoriesTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> categoriesList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT DISTINCT category FROM " + TABLE_NAME);
                while (rs.next()) {
                    String category = rs.getString("category");
                    if (!categoriesList.contains(category)) {
                        categoriesList.add(category);
                    }
                }
                rs.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return categoriesList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> categoriesList) {
            if (!categoriesList.isEmpty()) {
                categories.clear();
                categories.addAll(categoriesList);
                categoryAdapter.notifyDataSetChanged();
            }
        }
    }

    private class AddItemTask extends AsyncTask<Void, Void, Boolean> {

        private String category, itemName, notificationLength, unit;

        public AddItemTask(String category, String itemName) {
            this.category = category;
            this.itemName = itemName;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = false;
            new Table(USER_TABLE_NAME, TableType.GROCERY);
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                String query = "INSERT INTO " + USER_TABLE_NAME + " (name, category)" +
                        "VALUES ('" + itemName + "', '" + category + "')";
                //String query = "INSERT INTO " + USER_TABLE_NAME + " (category, name, expiration_date, notification_length, unit) VALUES ('"
                //        + category + "', '" + itemName +  "', '" + notificationLength + "', '" + unit + "')";
                statement.executeUpdate(query);
                statement.close();
                connection.close();
                success = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(GroceryActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GroceryActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
