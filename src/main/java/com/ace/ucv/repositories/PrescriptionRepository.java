package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
public class PrescriptionRepository {
    private static final Logger logger = LogManager.getLogger(PrescriptionRepository.class);

    private static final String UPDATE_PRESCRIPTION_SQL =
            "UPDATE prescriptions SET date = ?, patient_id = ?, disease_id = ?, medication_id = ? WHERE id = ?";
    private static final String DELETE_PRESCRIPTION_SQL =
            "DELETE FROM prescriptions WHERE id = ?";
    private static final String SELECT_PRESCRIPTION_SQL =
            "SELECT p.id, p.date, p.patient_id, pt.name as patient_name, d.name as disease_name, m.name as medication_name " +
                    "FROM prescriptions p " +
                    "JOIN patients pt ON p.patient_id = pt.id " +
                    "JOIN diseases d ON p.disease_id = d.id " +
                    "JOIN medications m ON p.medication_id = m.id";

    private static final String INSERT_PRESCRIPTION_SQL = "INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)";
    public static final String ORDER_BY_ID_DESC_LIMIT_SQL = "SELECT id FROM prescriptions ORDER BY id DESC LIMIT 1";

    /**
     * Retrieves the ID for a given item name from a specified table.
     *
     * @param tableName The name of the table.
     * @param itemName The name of the item.
     * @return The ID of the item or -1 if not found.
     */
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

    /**
     * Retrieves the last prescription ID from the database.
     *
     * @return The last prescription ID or -1 if not found.
     */
    public int getLastPrescriptionId() {
        int lastId = -1; // Initialize with an invalid value
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        lastId = getLastId(connection, statement, resultSet, lastId);

        return lastId;
    }

    private static int getLastId(Connection connection, Statement statement, ResultSet resultSet, int lastId) {
        try {
            connection = DatabaseManager.connect();
            statement = connection.createStatement();

            resultSet = statement.executeQuery(ORDER_BY_ID_DESC_LIMIT_SQL);

            if (resultSet.next()) {
                lastId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lastId;
    }

    /**
     * Loads items from a specified table in the database.
     *
     * @param tableName The name of the table.
     * @param columnName The name of the column to retrieve.
     * @return A List of items.
     */
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

    /**
     * Loads all prescriptions from the database.
     *
     * @return An ObservableList of Prescriptions.
     */
    public ObservableList<Prescription> loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(SELECT_PRESCRIPTION_SQL)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                int patientId = resultSet.getInt("patient_id"); // Retrieve patient_id
                String patientName = resultSet.getString("patient_name");
                String diseaseName = resultSet.getString("disease_name");
                String medicationName = resultSet.getString("medication_name");

                Prescription prescription = new Prescription(id, date, diseaseName, medicationName);
                prescription.setPatientId(patientId); // Set the patientId in Prescription
                prescription.setPatientName(patientName); // Set the patientName in Prescription

                prescriptions.add(prescription);
            }
        } catch (SQLException e) {
            handleDatabaseError("Error loading prescriptions from database: " + e.getMessage(), e);
        }
        return prescriptions;
    }


    /**
     * Edits an existing prescription in the database.
     *
     * @param id The ID of the prescription.
     * @param date The new date for the prescription.
     * @param patientId The ID of the patient.
     * @param diseaseId The ID of the disease.
     * @param medicationId The ID of the medication.
     * @return True if the update was successful, False otherwise.
     */
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

    /**
     * Deletes a prescription from the database.
     *
     * @param id The ID of the prescription to delete.
     * @return True if the deletion was successful, False otherwise.
     */
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
        if (shouldSavePrescription(diseaseId, medicationId)) {
            try (Connection connection = DatabaseManager.connect()) {
                connection.setAutoCommit(false);

                try {
                    int patientId = patient.getId();
                    int prescriptionId = insertPrescription(connection, date, patientId, diseaseId, medicationId);
                    if (prescriptionId != -1) {
                        connection.commit();
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

    private boolean shouldSavePrescription(String diseaseId, String medicationId) {
        return Integer.parseInt(diseaseId) != -1 && Integer.parseInt(medicationId) != -1;
    }

    private int insertPrescription(Connection connection, String date, int patientId, String diseaseId, String medicationId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRESCRIPTION_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, date);
            preparedStatement.setInt(2, patientId);
            preparedStatement.setString(3, diseaseId);
            preparedStatement.setString(4, medicationId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Handles errors related to database operations.
     *
     * @param errorMessage The error message to log.
     * @param e The SQLException that was thrown.
     */
    private void handleDatabaseError(String errorMessage, SQLException e) {
        logger.error(errorMessage, e);
        throw new RuntimeException(errorMessage, e);
    }
}