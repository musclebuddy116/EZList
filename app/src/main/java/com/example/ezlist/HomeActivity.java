package com.example.ezlist;

import android.os.Bundle;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**FIXME:
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