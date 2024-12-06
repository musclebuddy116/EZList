package com.example.ezlist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText itemNameInput, itemExpirationDateInput, daysBeforeInput;
    private Spinner categorySpinner, notificationUnitSpinner;
    private Button addItemButton, viewItemsButton;

    private ArrayList<String> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<CharSequence> unitAdapter;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemNameInput = findViewById(R.id.itemNameInput);
        itemExpirationDateInput = findViewById(R.id.itemExpirationDateInput);
        daysBeforeInput = findViewById(R.id.daysBeforeInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notificationUnitSpinner = findViewById(R.id.notificationUnitSpinner);
        addItemButton = findViewById(R.id.addItemButton);
        viewItemsButton = findViewById(R.id.viewPantry);

        categories.add("Dairy");
        categories.add("Meat");
        categories.add("Produce");
        categories.add("Bakery");
        categories.add("Other");

        categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        unitAdapter = ArrayAdapter.createFromResource(this, R.array.notification_units, R.layout.spinner_item);
        unitAdapter.setDropDownViewResource(R.layout.spinner_item);
        notificationUnitSpinner.setAdapter(unitAdapter);

        new LoadCategoriesTask().execute();

        calendar = Calendar.getInstance();
        itemExpirationDateInput.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                itemExpirationDateInput.setText(sdf.format(calendar.getTime()));
            }, year, month, day);
            datePickerDialog.show();
        });

        addItemButton.setOnClickListener(v -> {
            String itemName = itemNameInput.getText().toString().trim();
            String expirationDateString = itemExpirationDateInput.getText().toString().trim();
            String notificationLengthString = daysBeforeInput.getText().toString().trim();
            String selectedCategory = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";
            String unit = notificationUnitSpinner.getSelectedItem() != null ? notificationUnitSpinner.getSelectedItem().toString() : "";

            if (itemName.isEmpty()) {
                itemNameInput.setError("Item name is required");
                return;
            }
            if (expirationDateString.isEmpty()) {
                itemExpirationDateInput.setError("Expiration date is required");
                return;
            }
            if (notificationLengthString.isEmpty()) {
                daysBeforeInput.setError("Notification length is required");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date expirationDate;
            try {
                expirationDate = sdf.parse(expirationDateString);
            } catch (ParseException e) {
                itemExpirationDateInput.setError("Invalid date format");
                return;
            }

            int notificationLength;
            try {
                notificationLength = Integer.parseInt(notificationLengthString);
            } catch (NumberFormatException e) {
                daysBeforeInput.setError("Invalid number");
                return;
            }

            new AddItemTask(selectedCategory, itemName, expirationDateString, notificationLengthString, unit).execute();

            itemNameInput.setText("");
            itemExpirationDateInput.setText("");
            daysBeforeInput.setText("");
        });

        viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });
    }

    private class LoadCategoriesTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> categoriesList = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT DISTINCT category FROM " + Global.MAIN_TABLE_NAME);
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

        private String category, itemName, expirationDate, notificationLength, unit;

        public AddItemTask(String category, String itemName, String expirationDate, String notificationLength, String unit) {
            this.category = category;
            this.itemName = itemName;
            this.expirationDate = expirationDate;
            this.notificationLength = notificationLength;
            this.unit = unit;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = false;
            try {
                Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                Statement statement = connection.createStatement();
                String query = "INSERT INTO " + Global.MAIN_TABLE_NAME + " (category, name, expiration_date, notification_length, unit) VALUES ('"
                        + category + "', '" + itemName + "', '" + expirationDate + "', '" + notificationLength + "', '" + unit + "')";
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
                Toast.makeText(MainActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
