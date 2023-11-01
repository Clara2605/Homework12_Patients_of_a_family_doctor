package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.services.interfaces.IPatientService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientService implements IPatientService {

    public void addPatient(Patient patient) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO patients (name, age, field_of_work) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, patient.getName());
            preparedStatement.setInt(2, patient.getAge());
            preparedStatement.setString(3, patient.getFieldOfWork());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Patient> loadPatientsFromDatabase() {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM patients");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String fieldOfWork = resultSet.getString("field_of_work");
                patients.add(new Patient(id, name, age, fieldOfWork));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public void editPatient(Patient patient, String newName, int newAge, String newFieldOfWork) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE patients SET name=?, age=?, field_of_work=? WHERE id=?")) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, newAge);
            preparedStatement.setString(3, newFieldOfWork);
            preparedStatement.setInt(4, patient.getId());
            preparedStatement.executeUpdate();

            patient.setName(newName);
            patient.setAge(newAge);
            patient.setFieldOfWork(newFieldOfWork);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(Patient patient) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM patients WHERE id=?")) {
            preparedStatement.setInt(1, patient.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
