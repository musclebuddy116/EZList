package com.example.ezlist;

public class Item {
    public int id;
    public String name;
    public String category;
    public int shelf_life;

    public Item(int id, String name, String category, int shelf_life) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.shelf_life = shelf_life;
    }
}
