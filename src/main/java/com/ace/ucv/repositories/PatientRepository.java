package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {
    private static final Logger logger = LogManager.getLogger(PatientRepository.class);

    private static final String INSERT_PATIENT_SQL =
            "INSERT INTO patients (name, age, field_of_work) VALUES (?, ?, ?)";
    private static final String UPDATE_PATIENT_SQL =
            "UPDATE patients SET name=?, age=?, field_of_work=? WHERE id=?";
    private static final String DELETE_PATIENT_SQL =
            "DELETE FROM patients WHERE id=?";
    private static final String SELECT_ALL_PATIENTS_SQL =
            "SELECT * FROM patients";

    public void addPatient(Patient patient) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_PATIENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, patient.getName());
            preparedStatement.setInt(2, patient.getAge());
            preparedStatement.setString(3, patient.getFieldOfWork());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                }
            }
            logger.info(String.format("Patient added successfully: %s", patient.getName()));
        } catch (SQLException e) {
            handleDatabaseError("Error adding patient: " + e.getMessage(), e);
        }
    }

    public List<Patient> loadPatientsFromDatabase() {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PATIENTS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                patients.add(extractPatientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            handleDatabaseError("Error loading patients from database: " + e.getMessage(), e);
        }
        return patients;
    }

    private Patient extractPatientFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int age = resultSet.getInt("age");
        String fieldOfWork = resultSet.getString("field_of_work");
        return new Patient(id, name, age, fieldOfWork);
    }

    public void editPatient(Patient patient, String newName, int newAge, String newFieldOfWork) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PATIENT_SQL)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, newAge);
            preparedStatement.setString(3, newFieldOfWork);
            preparedStatement.setInt(4, patient.getId());
            preparedStatement.executeUpdate();

            patient.setName(newName);
            patient.setAge(newAge);
            patient.setFieldOfWork(newFieldOfWork);
            logger.info(String.format("Patient edited successfully: %d", patient.getId()));
        } catch (SQLException e) {
            handleDatabaseError("Error editing patient: " + e.getMessage(), e);
        }
    }

    public void deletePatient(Patient patient) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PATIENT_SQL)) {
            preparedStatement.setInt(1, patient.getId());
            preparedStatement.executeUpdate();
            logger.info(String.format("Patient deleted successfully: %d", patient.getId()));
        } catch (SQLException e) {
            handleDatabaseError("Error deleting patient: " + e.getMessage(), e);
        }
    }

    private void handleDatabaseError(String errorMessage, SQLException e) {
        logger.error(errorMessage, e);
        throw new RuntimeException(errorMessage, e);
    }
}
