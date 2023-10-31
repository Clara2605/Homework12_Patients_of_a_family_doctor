package com.ace.ucv.model;

public class Medication {
    private int id;
    private String name;
    private String category;

    public Medication(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
