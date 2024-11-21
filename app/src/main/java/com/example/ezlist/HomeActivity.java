package com.example.ezlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize welcome message and buttons for navigation
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button viewGroceryList = findViewById(R.id.viewGroceryList);
        Button viewItemsButton = findViewById(R.id.viewPantry);

        // Navigate to MainActivity for item entry when "Add Item" is clicked
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GroceryActivity.class);
            startActivity(intent);
        });

        viewGroceryList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ViewGroceryListActivity.class);
            startActivity(intent);
        });

        // Navigate to SavedItemsActivity when "View Pantry" is clicked
        viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        /** FIXME:
         * Get username from MainActivity passed in here, and other relevant information
         * Create TableManipulator, make user's GroceryList table and Pantry table
         * Implement UI
         *
         * Nearing Expiration: scrollable list of items in pantry nearing expiration
         * Add to Grocery List: narrowing down search bar, add button
         * Go Shopping: Button to view grocery list
         * View Pantry: Button to view pantry
         */
    }
}
