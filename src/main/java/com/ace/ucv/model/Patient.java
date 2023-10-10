package com.ace.ucv.model;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private String name;
    private int age;
    private String fieldOfWork;
    private List<Disease> diseases = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();

    public Patient(String name, int age, String fieldOfWork) {
        this.name = name;
        this.age = age;
        this.fieldOfWork = fieldOfWork;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getFieldOfWork() {
        return fieldOfWork;
    }

    public void addDisease(Disease disease) {
        diseases.add(disease);
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public int getPrescriptionCount() {
        return prescriptions.size();
    }

    public boolean hasDisease(Disease disease) {
        return diseases.contains(disease);
    }

    public boolean hasTreatment(Medication medication) {
        for (Prescription prescription : prescriptions) {
            if (prescription.getMedication().equals(medication)) {
                return true;
            }
        }
        return false;
    }
}
