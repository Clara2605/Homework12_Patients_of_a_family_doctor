package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepository {
    private static final Logger logger = LogManager.getLogger(PrescriptionRepository.class);

    private static final String UPDATE_PRESCRIPTION_SQL =
            "UPDATE prescriptions SET date = ?, patient_id = ?, disease_id = ?, medication_id = ? WHERE id = ?";
    private static final String DELETE_PRESCRIPTION_SQL =
            "DELETE FROM prescriptions WHERE id = ?";
    private static final String SELECT_PRESCRIPTION_SQL =
            "SELECT p.id, p.date, d.name as disease_name, m.name as medication_name " +
                    "FROM prescriptions p " +
                    "JOIN diseases d ON p.disease_id = d.id " +
                    "JOIN medications m ON p.medication_id = m.id";
    public int getIdFromName(String tableName, String itemName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM " + tableName + " WHERE name = ?")) {
            preparedStatement.setString(1, itemName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            handleDatabaseError("Error getting ID from name: " + e.getMessage(), e);
        }
        return -1;
    }

    public List<String> loadItemsFromDatabase(String tableName, String columnName) {
        List<String> items = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + columnName + " FROM " + tableName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString(columnName);
                items.add(name);
            }
        } catch (SQLException e) {
            handleDatabaseError("Error loading items from database: " + e.getMessage(), e);
        }
        return items;
    }

    public ObservableList<Prescription> loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(SELECT_PRESCRIPTION_SQL)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String diseaseName = resultSet.getString("disease_name");
                String medicationName = resultSet.getString("medication_name");

                prescriptions.add(new Prescription(id, date, diseaseName, medicationName));
            }
        } catch (SQLException e) {
            handleDatabaseError("Error loading prescriptions from database: " + e.getMessage(), e);
        }
        return prescriptions;
    }

    public boolean editPrescription(int id, String date, int patientId, int diseaseId, int medicationId) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRESCRIPTION_SQL)) {

            statement.setString(1, date);
            statement.setInt(2, patientId);
            statement.setInt(3, diseaseId);
            statement.setInt(4, medicationId);
            statement.setInt(5, id);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            handleDatabaseError("Error editing prescription: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean deletePrescription(int id) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRESCRIPTION_SQL)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            handleDatabaseError("Error deleting prescription: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean savePrescription(Patient patient, String date, String diseaseId, String medicationId) {
        int patientId = patient.getId();

        if (Integer.valueOf(diseaseId) != -1 && Integer.valueOf(medicationId) != -1) {
            try (Connection connection = DatabaseManager.connect()) {
                connection.setAutoCommit(false);

                String insertPrescriptionSQL = "INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPrescriptionSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, date);
                    preparedStatement.setInt(2, patientId);
                    preparedStatement.setString(3, diseaseId);
                    preparedStatement.setString(4, medicationId);
                    preparedStatement.executeUpdate();

                    connection.commit();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int prescriptionId = generatedKeys.getInt(1);
                        // Actualizarea listei de prescripții poate fi realizată aici sau la nivelul interfeței utilizator
                        return true;
                    }
                } catch (SQLException e) {
                    handleDatabaseError("Error saving prescription: " + e.getMessage(), e);
                    connection.rollback();
                }
            } catch (SQLException e) {
                handleDatabaseError("Error connecting to the database: " + e.getMessage(), e);
            }
        }
        return false;
    }

    private void handleDatabaseError(String errorMessage, SQLException e) {
        logger.error(errorMessage, e);
        throw new RuntimeException(errorMessage, e);
    }
}
