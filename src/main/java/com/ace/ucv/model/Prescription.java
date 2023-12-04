package com.ace.ucv.model;

public class Prescription {
    private int id;
    private String date;

    private String disease; // Store as a String
    private String medication; // Store as a String

    private int diseaseId;
    private int medicationId;


    public Prescription(int id, String date, String disease, String medication) {
        this.id = id;
        this.date = date;
        this.disease = disease;
        this.medication = medication;
    }

    public Prescription(int id, String date, String disease, String medication, int diseaseId, int medicationId) {
        this.id = id;
        this.date = date;
        this.disease = disease;
        this.medication = medication;
        this.diseaseId = diseaseId;
        this.medicationId = medicationId;
    }

    public int getId() {
        return id;
    }
    public void setId(int lastInsertedPrescriptionId) {
        this.id = lastInsertedPrescriptionId;
    }
    public String getDate() {
        return date;
    }

    public String getDisease() {
        return disease;
    }

    public String getMedication() {
        return medication;
    }

    public int getDiseaseId() {
        return diseaseId;
    }

    public int getMedicationId() {
        return medicationId;
    }
}