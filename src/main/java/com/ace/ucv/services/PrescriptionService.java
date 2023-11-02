package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService implements IPrescriptionService {

    private static final Logger logger = LogManager.getLogger(PrescriptionService.class);

    public int getIdFromName(String tableName, String itemName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM " + tableName + " WHERE name = ?")) {
            preparedStatement.setString(1, itemName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting ID from name %s", e.getMessage()));
            throw new RuntimeException(String.format("Error getting ID from name: %s", e.getMessage()));
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
            logger.error(String.format("Error loading items from database%s", e.getMessage()));
            throw new RuntimeException(String.format("Error loading items from database: %s", e.getMessage()));
        }
        return items;
    }

    public ObservableList<Prescription> loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT p.id, p.date, d.name as disease_name, m.name as medication_name " +
                             "FROM prescriptions p " +
                             "JOIN diseases d ON p.disease_id = d.id " +
                             "JOIN medications m ON p.medication_id = m.id")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String diseaseName = resultSet.getString("disease_name");
                String medicationName = resultSet.getString("medication_name");

                prescriptions.add(new Prescription(id, date, diseaseName, medicationName));
            }
        } catch (SQLException e) {
            logger.error(String.format("Error loading prescriptions from database %s", e.getMessage()));
            throw new RuntimeException(String.format("Error loading prescriptions from database: %s", e.getMessage()));
        }
        return prescriptions;
    }

    public boolean editPrescription(int id, String date, int patientId, int diseaseId, int medicationId) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE prescriptions SET date = ?, patient_id = ?, disease_id = ?, medication_id = ? WHERE id = ?")) {

            statement.setString(1, date);
            statement.setInt(2, patientId);
            statement.setInt(3, diseaseId);
            statement.setInt(4, medicationId);
            statement.setInt(5, id);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            logger.error(String.format("Error editing prescription %s", e.getMessage()));
            throw new RuntimeException(String.format("Error editing prescription: %s", e.getMessage()));
        }
    }

    public boolean deletePrescription(int id) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM prescriptions WHERE id = ?")) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            logger.error(String.format("Error deleting prescription %s", e.getMessage()));
            throw new RuntimeException(String.format("Error deleting prescription: %s", e.getMessage()));
        }
    }
}
