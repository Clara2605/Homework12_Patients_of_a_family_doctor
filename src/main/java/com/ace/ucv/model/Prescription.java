package com.ace.ucv.model;

import java.time.LocalDate;

public class Prescription {
    private int id;
    private LocalDate date;

    private String disease; // Store as a String
    private String medication; // Store as a String


    public Prescription(int id, LocalDate date, String disease2, String medication) {
        this.id = id;
        this.date = date;
        this.disease = disease2;
        this.medication = medication;
    }

    public int getId() {
        return id;
    }
    public void setId(int lastInsertedPrescriptionId) {
        this.id = lastInsertedPrescriptionId;
    }
    public LocalDate getDate() {
        return date;
    }

    public String getDisease() {
        return disease;
    }

    public String getMedication() {
        return medication;
    }
}
