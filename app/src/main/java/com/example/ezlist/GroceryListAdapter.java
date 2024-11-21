package com.example.ezlist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.cursoradapter.widget.CursorAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class GroceryListAdapter extends BaseAdapter {

    private Context context;
    private List<Item> items;
    private DatabaseHelper dbHelper;

    public GroceryListAdapter(Context context, List<Item> items, DatabaseHelper dbHelper) {;
        this.context = context;
        this.items = items;
        this.dbHelper = dbHelper;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return items.get(position).id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_grocery, parent, false);
        }

        TextView itemName = convertView.findViewById(R.id.item_name);
        CheckBox itemCheckbox = convertView.findViewById(R.id.item_checkbox);

        Item item = items.get(position);
        itemName.setText(item.name);

        itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    dbHelper.moveItemToPantry(item.name, item.category, item.shelf_life);
                    items.remove(position);
                    notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}