package com.example.ezlist;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, emailInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Link UI components with XML layout by their IDs
        usernameInput = findViewById(R.id.usernameInput); // Input field for the username
        passwordInput = findViewById(R.id.passwordInput); // Input field for the password
        emailInput = findViewById(R.id.emailInput); // Input field for the email (optional)
        registerButton = findViewById(R.id.registerButton); // Button to submit registration

        // Set a click listener for the register button to handle the button click event
        registerButton.setOnClickListener(v -> {
            // Retrieve text entered by the user in the username, password, and email fields
            String username = usernameInput.getText().toString().trim(); // Get username and remove spaces
            String password = passwordInput.getText().toString().trim(); // Get password and remove spaces
            String email = emailInput.getText().toString().trim(); // Get email and remove spaces

            // Check if username or password fields are empty
            if (username.isEmpty() || password.isEmpty()) {
                // Display a message if either field is empty
                Toast.makeText(this, "Username and password are required", Toast.LENGTH_SHORT).show();
                return; // Stop further execution if validation fails
            }

            // Call the registerUser method to register the user with the given details
            registerUser(username, password, email);
        });
    }

    // Method to hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Initialize SHA-256 hashing algorithm
            byte[] hashedBytes = digest.digest(password.getBytes()); // Hash the password and get the bytes
            StringBuilder hexString = new StringBuilder();

            // Convert hashed bytes into a hexadecimal string
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b); // Convert each byte to hex
                if (hex.length() == 1) hexString.append('0'); // Pad with zero if needed
                hexString.append(hex); // Append hex value to the string
            }

            return hexString.toString(); // Return the hashed password as a hex string

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Return null if hashing fails
        }
    }

    // Method to register the user, using the provided username, password, and email
    private void registerUser(String username, String password, String email) {
        String passwordHash = hashPassword(password); // Hash the password

        if (passwordHash == null) {
            Toast.makeText(this, "Error hashing password", Toast.LENGTH_SHORT).show();
            return; // Exit if hashing fails
        }

        // Database insertion in a background thread
        new Thread(() -> {
            try (Connection connection = DatabaseHelper.getConnection()) {
                String insertQuery = "INSERT INTO user_accounts (username, password_hash, email) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, username); // Set the username in the query
                preparedStatement.setString(2, passwordHash); // Set the hashed password
                preparedStatement.setString(3, email.isEmpty() ? null : email); // Set the email if provided

                int rowsAffected = preparedStatement.executeUpdate(); // Execute the insert statement

                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
