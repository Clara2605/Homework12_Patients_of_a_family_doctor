package com.ace.ucv.model;

import com.ace.ucv.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Medication {
    private int id;
    private String name;
    private String category;
    private int count;

    public Medication(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
    public Medication(int id, String name, String category, int count) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.count = count;
    }
    public Medication(String name, String category) {
        this.name = name;
        this.category = category;
    }
    // Getter and setter for count
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int anInt) {
        this.id = anInt;
    }

}
