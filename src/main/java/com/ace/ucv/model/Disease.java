package com.ace.ucv.model;


public class Disease {
    private int id;
    private String name;

    public Disease(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Disease(String name) {
        this.name = name;
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
}
