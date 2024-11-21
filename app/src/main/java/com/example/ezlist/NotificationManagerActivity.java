package com.example.ezlist;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationManagerActivity extends AppCompatActivity { // Renamed to avoid conflict
    private Handler handler = new Handler(Looper.getMainLooper());

    private EditText itemNameInput;
    private ListView searchResultsListView;
    private ArrayAdapter<String> searchResultsAdapter;
    private ArrayList<String> searchResultsList = new ArrayList<>();
    private ArrayList<Integer> shelfLife = new ArrayList<>();
    private ArrayList<Integer> shelfList = new ArrayList<>();
    private final Set<String> notifiedItems = new HashSet<>();


    private Button submitButton;

    public class Item {
        private String name;
        private int shelfLife;

        // Constructor
        public Item(String name, int shelfLife) {
            this.name = name;
            this.shelfLife = shelfLife;
        }

        public String getName() {
            return name;
        }

        // Getter for shelf life
        public int getShelfLife() {
            return shelfLife;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        itemNameInput = findViewById(R.id.itemNameInput);
        searchResultsListView = findViewById(R.id.searchResultsListView);
        submitButton = findViewById(R.id.submitButton);


        searchResultsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResultsList);
        searchResultsListView.setAdapter(searchResultsAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "NotificationPermission"})
            @Override
            public void onClick(View v) {
                String searchQuery = itemNameInput.getText().toString().trim();
                if (!searchQuery.isEmpty()) {
                    new SearchItemTask(searchQuery).execute();
                } else {
                    Toast.makeText(NotificationManagerActivity.this, "Please enter an item name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (searchText.isEmpty()) {
                    searchResultsListView.setVisibility(View.GONE);
                } else {
                    new SearchItemTask(searchText).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchResultsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = searchResultsList.get(position);

            // Set the selected item in the itemNameInput field
            itemNameInput.setText(selectedItem);

            searchResultsListView.setVisibility(View.GONE);
        });

    }

    private class SearchItemTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private String searchQuery;

        public SearchItemTask(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> itemsList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                String query = "SELECT name, shelf_life FROM " + Global.MAIN_TABLE_NAME + " WHERE name LIKE '" + searchQuery + "%'";
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    itemsList.add(rs.getString("name"));
                    shelfList.clear(); // Clear previous values
                    shelfList.add(rs.getInt("shelf_life"));

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
            if (itemsList.size() == 1 && !shelfList.isEmpty()) {
                itemNameInput.setText(itemsList.get(0));
                int shelfLifeMinutes = shelfList.get(0);
                long delayInMillis = shelfLifeMinutes  * 1000;
                handler.postDelayed(() -> sendNotification(itemsList.get(0)), delayInMillis);
                searchResultsListView.setVisibility(View.GONE);
            } else {
                displayFilteredItems(itemsList);
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

        private void sendNotification(String itemName) {
            if (notifiedItems.contains(itemName)) {
                Log.d("NotificationManager", "Notification for " + itemName + " already sent.");
                return; // Skip if already notified
            }

            // Mark as notified
            notifiedItems.add(itemName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "My Notification",
                        "Item Expiration Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationManagerActivity.this, "My Notification")
                    .setContentTitle("Item Expiring Soon")
                    .setContentText(itemName + " is about to expire")
                    .setSmallIcon(R.drawable.shoppingcart)
                    .setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationManagerActivity.this);
            if (ActivityCompat.checkSelfPermission(NotificationManagerActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                managerCompat.notify(1, builder.build());
            }
        }


        // Remember to stop the Handler callbacks when the activity is destroyed
    }
}


