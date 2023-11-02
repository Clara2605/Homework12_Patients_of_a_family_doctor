package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import com.ace.ucv.services.interfaces.IMedicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicationService implements IMedicationService {

    private static final Logger logger = LoggerFactory.getLogger(MedicationService.class);

    @Override
    public void addMedication(Medication medication) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO medications (name, category) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, medication.getName());
            preparedStatement.setString(2, medication.getCategory());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medication.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding medication", e);
            throw new RuntimeException("Error adding medication", e);
        }
    }

    @Override
    public void editMedication(Medication medication, String editedName, String editedCategory) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE medications SET name=?, category=? WHERE id=?")) {
            preparedStatement.setString(1, editedName);
            preparedStatement.setString(2, editedCategory);
            preparedStatement.setInt(3, medication.getId());
            preparedStatement.executeUpdate();

            medication.setName(editedName);
            medication.setCategory(editedCategory);
        } catch (SQLException e) {
            logger.error("Error editing medication", e);
            throw new RuntimeException("Error editing medication", e);
        }
    }

    @Override
    public void deleteMedication(Medication medication) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM medications WHERE id=?")) {
            preparedStatement.setInt(1, medication.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting medication", e);
            throw new RuntimeException("Error deleting medication", e);
        }
    }

    @Override
    public List<Medication> loadMedicationsFromDatabase() {
        List<Medication> medications = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM medications")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                Medication medication = new Medication(id, name, category);
                medications.add(medication);
            }
        } catch (SQLException e) {
            logger.error("Error loading medications from database", e);
            throw new RuntimeException("Error loading medications from database", e);
        }
        return medications;
    }
}
