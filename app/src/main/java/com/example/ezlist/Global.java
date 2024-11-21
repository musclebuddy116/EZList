package com.example.ezlist;

import android.app.Application;

public class Global extends Application {
    private static String username;
    static final String DATABASE_NAME = "grocery_store_data";
    static final String URL = "jdbc:mysql://18.117.171.203:3306/" + DATABASE_NAME;
    static final String USER = "android";
    static final String PASSWORD = "android";
    static String USER_TABLE_NAME;

    public static void setUsername(String username) {
        Global.username = username;
    }

    public static void setUserTableName(String userTableNameStem) {
        Global.USER_TABLE_NAME = username + userTableNameStem;
    }

    public static String getUsername() {
        return username;
    }

    public static String getUserTableName() {
        return USER_TABLE_NAME;
    }
}
