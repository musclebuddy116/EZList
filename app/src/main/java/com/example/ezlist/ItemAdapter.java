package com.example.ezlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> items;
    private String tableName;

    public ItemAdapter(Context context, ArrayList<String> items, String tableName) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.tableName = tableName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_with_button, parent, false);
        }

        TextView itemTextView = convertView.findViewById(R.id.itemTextView);
        ImageButton removeButton = convertView.findViewById(R.id.removeButton);

        // Set the item text
        String item = items.get(position);
        itemTextView.setText(item);

        // Handle the remove button click
        removeButton.setOnClickListener(v -> {
            String itemName = item.split(" \\(")[0]; // Extract the name from the string
            removeItemFromDatabase(itemName, position);
        });

        return convertView;
    }

    @SuppressLint("StaticFieldLeak")
    private void removeItemFromDatabase(String itemName, int position) {
        new AsyncTask<Void, Void, Boolean>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    Connection connection = DriverManager.getConnection(Global.URL, Global.USER, Global.PASSWORD);
                    Statement statement = connection.createStatement();
                    // Delete the item from the database
                    int rowsAffected = statement.executeUpdate(
                            "DELETE FROM " + tableName + " WHERE name = '" + itemName + "'");
                    statement.close();
                    connection.close();
                    return rowsAffected > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Remove the item from the list and update the adapter
                    items.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
