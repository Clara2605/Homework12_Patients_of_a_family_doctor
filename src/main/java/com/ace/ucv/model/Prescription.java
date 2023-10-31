package com.ace.ucv.model;

import java.time.LocalDate;

public class Prescription {
    private int id;
    private LocalDate date;
    private Disease disease;
    private Medication medication;

    private String disease2; // Store as a String
    private String medication2; // Store as a String

    private int diseaseId; // Store the ID of the disease
    private int medicationId; // Store the ID of the medication

    public Prescription(int id, LocalDate date, Disease disease, Medication medication) {
        this.id = id;
        this.date = date;
        this.disease = disease;
        this.medication = medication;
    }

    public Prescription(int id, LocalDate date, String disease2, String medication2) {
        this.id = id;
        this.date = date;
        this.disease2 = disease2;
        this.medication2 = medication2;
    }

    public int getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(int diseaseId) {
        this.diseaseId = diseaseId;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public Prescription(int id, LocalDate date, int diseaseId, int medicationId) {
        this.id = id;
        this.date = date;
        this.diseaseId = diseaseId;
        this.medicationId = medicationId;
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

    public Disease getDisease() {
        return disease;
    }

    public Medication getMedication() {
        return medication;
    }
    public String getDisease2() {
        return disease2;
    }

    public String getMedication2() {
        return medication2;
    }
}
