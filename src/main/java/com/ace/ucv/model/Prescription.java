package com.ace.ucv.model;

public class Prescription {
    private Medication medication;
    private int prescriptionCount;

    public Prescription(Medication medication, int prescriptionCount) {
        this.medication = medication;
        this.prescriptionCount = prescriptionCount;
    }

    public Medication getMedication() {
        return medication;
    }

    public int getPrescriptionCount() {
        return prescriptionCount;
    }
}
