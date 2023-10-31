package com.ace.ucv.model;

import java.time.LocalDate;

public class Prescription {
    private int id;
    private LocalDate date;
    private Disease disease;
    private Medication medication;

    public Prescription(int id, LocalDate date, Disease disease, Medication medication) {
        this.id = id;
        this.date = date;
        this.disease = disease;
        this.medication = medication;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Disease getDisease() {
        return disease;
    }

    public Medication getMedication() {
        return medication;
    }
}
