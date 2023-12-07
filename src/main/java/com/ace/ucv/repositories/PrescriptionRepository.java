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
            "SELECT p.id, p.date, d.name as disease_name, m.name as medication_name " +
                    "FROM prescriptions p " +
                    "JOIN diseases d ON p.disease_id = d.id " +
                    "JOIN medications m ON p.medication_id = m.id";
    private static final String INSERT_PRESCRIPTION_SQL = "INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)";
    public static final String ORDER_BY_ID_DESC_LIMIT_SQL = "SELECT id FROM prescriptions ORDER BY id DESC LIMIT 1";

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
    public int getLastPrescriptionId() {
        int lastId = -1; // Initialize with an invalid value
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseManager.connect();/* Get database connection, e.g., DriverManager.getConnection(...) */;
            statement = connection.createStatement();

            resultSet = statement.executeQuery(ORDER_BY_ID_DESC_LIMIT_SQL);

            if (resultSet.next()) {
                lastId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
        } finally {
            // Close resources to avoid memory leaks
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

    private void handleDatabaseError(String errorMessage, SQLException e) {
        logger.error(errorMessage, e);
        throw new RuntimeException(errorMessage, e);
    }
}