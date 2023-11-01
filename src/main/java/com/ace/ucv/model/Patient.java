package com.ace.ucv.model;

import com.ace.ucv.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String fieldOfWork;
    private List<Disease> diseases = new ArrayList<>();
    private String diseaseName;

    private String medicationName;
    private List<Prescription> prescriptions = new ArrayList<>();

   // public Patient(int id, String name, int age, String fieldOfWork) {
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

    // Setter for medicationName
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
    public int getId() {
        return id;
    }
    public String getFieldOfWork() {
        return fieldOfWork;
    }

    public void insertIntoDatabase() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO patients (name, age, field_of_work) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, fieldOfWork);
            preparedStatement.executeUpdate();

            // După inserare, obțineți ID-ul pacientului inserat
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // Aici obțineți ID-ul generat
                    System.out.println("ID-ul pacientului inserat este: " + generatedId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPatient(String name, int age, String fieldOfWork) {
        // Implementarea adăugării unui pacient în baza de date
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO patients (name, age, field_of_work) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, fieldOfWork);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Patient> loadPatientsFromDatabase() {
        // Implementarea încărcării pacienților din baza de date
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM patients");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String fieldOfWork = resultSet.getString("field_of_work");
                Patient patient = new Patient(name, age, fieldOfWork);
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public void editPatient(String editedName, int editedAge, String editedFieldOfWork) {
        // Implementarea editării pacientului în baza de date
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE patients SET name=?, age=?, field_of_work=? WHERE name=?")) {
            preparedStatement.setString(1, editedName);
            preparedStatement.setInt(2, editedAge);
            preparedStatement.setString(3, editedFieldOfWork);
            preparedStatement.setString(4, this.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Actualizarea pacientului curent
        this.name = editedName;
        this.age = editedAge;
        this.fieldOfWork = editedFieldOfWork;
    }

    public void deletePatient() {
        // Implementarea ștergerii pacientului din baza de date
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM patients WHERE name=?")) {
            preparedStatement.setString(1, this.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
