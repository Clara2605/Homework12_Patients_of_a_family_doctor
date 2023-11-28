package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicationRepository {
    private static final Logger logger = LogManager.getLogger(MedicationRepository.class);

    private static final String INSERT_MEDICATION_SQL =
            "INSERT INTO medications (name, category) VALUES (?, ?)";
    private static final String UPDATE_MEDICATION_SQL =
            "UPDATE medications SET name=?, category=? WHERE id=?";
    private static final String DELETE_MEDICATION_SQL =
            "DELETE FROM medications WHERE id=?";
    private static final String SELECT_MEDICATION_SQL =
            "SELECT * FROM medications";

    public void addMedication(Medication medication) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_MEDICATION_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, medication.getName());
            preparedStatement.setString(2, medication.getCategory());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medication.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error adding medication", e);
        }
    }

    public void editMedication(Medication medication, String editedName, String editedCategory) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEDICATION_SQL)) {
            preparedStatement.setString(1, editedName);
            preparedStatement.setString(2, editedCategory);
            preparedStatement.setInt(3, medication.getId());
            preparedStatement.executeUpdate();

            medication.setName(editedName);
            medication.setCategory(editedCategory);
        } catch (SQLException e) {
            handleSQLException("Error editing medication", e);
        }
    }

    public void deleteMedication(Medication medication) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MEDICATION_SQL)) {
            preparedStatement.setInt(1, medication.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error deleting medication", e);
        }
    }

    public List<Medication> loadMedicationsFromDatabase() {
        List<Medication> medications = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_MEDICATION_SQL)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                Medication medication = new Medication(id, name, category);
                medications.add(medication);
            }
        } catch (SQLException e) {
            handleSQLException("Error loading medications from database", e);
        }
        return medications;
    }

    private void handleSQLException(String errorMessage, SQLException e) {
        logger.error(String.format("%s %s", errorMessage, e.getMessage()));
        throw new RuntimeException(String.format("%s %s", errorMessage, e.getMessage()));
    }
}