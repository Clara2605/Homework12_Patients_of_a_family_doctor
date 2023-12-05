package com.ace.ucv.model;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String fieldOfWork;
    private String diseaseName;

    private String medicationName;

     public Patient( String name, int age, String fieldOfWork) {
       // this.id = id;
        this.name = name;
        this.age = age;
        this.fieldOfWork = fieldOfWork;
    }

     public Patient(int id, String name, int age, String fieldOfWork) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.fieldOfWork = fieldOfWork;
    }

    public Patient(int id, String name, int age, String fieldOfWork, String diseaseOrMedicationName, boolean isDisease) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.fieldOfWork = fieldOfWork;
        if (isDisease) {
            this.diseaseName = diseaseOrMedicationName;
        } else {
            this.medicationName = diseaseOrMedicationName;
        }
    }


    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }
    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFieldOfWork() {
        return fieldOfWork;
    }
    public void setFieldOfWork(String fieldOfWork) {
        this.fieldOfWork = fieldOfWork;
    }

}
