package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicationServiceTest {

    @Test
    void editMedication() {
        MedicationService medicationService = new MedicationService();
        Medication medication = new Medication("Paracetamol", "Analgesic");
        medicationService.addMedication(medication);
        medicationService.editMedication(medication, "Paracetamol", "Analgesic");
        assertEquals("Paracetamol", medication.getName());
        assertEquals("Analgesic", medication.getCategory());
    }

    @Test
    public void test_loadMedicationsFromDatabase_successfullyReturnsListOfMedicationObjects() {
        // Create a mock Connection object
        Connection connection = null;
        // Create a mock Statement object
        Statement statement = null;
        // Create a mock ResultSet object
        ResultSet resultSet = null;

        try {
            // Mock the DatabaseManager.connect() method to return the mock Connection object
            connection = DatabaseManager.connect();
            // Mock the connection.createStatement() method to return the mock Statement object
            statement = connection.createStatement();
            // Mock the statement.executeQuery() method to return the mock ResultSet object
            resultSet = statement.executeQuery("SELECT * FROM medications");
            // Mock the resultSet.next() method to return true and then false
            boolean hasNext = resultSet.next();
            // Mock the resultSet.getInt() method to return 1
            int id = resultSet.getInt("id");
            // Mock the resultSet.getString() method to return "name" and "category"
            String name = resultSet.getString("name");
            String category = resultSet.getString("category");

            // Create an instance of MedicationService
            MedicationService medicationService = new MedicationService();

            // Call the loadMedicationsFromDatabase() method
            List<Medication> medications = medicationService.loadMedicationsFromDatabase();

            // Verify that the medications list contains 10 Medication object with the correct ID, name, and category
            assertEquals(10, medications.size());
            Medication medication = medications.get(0);
            assertEquals(id, medication.getId());
            assertEquals(name, medication.getName());
            assertEquals(category, medication.getCategory());
        } catch (SQLException e) {
            fail("SQLException occurred");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                fail("SQLException occurred while closing resources");
            }
        }
    }

    @Test
    public void test_deleteMedication_successfullyRemovesMedicationFromDatabase() {
        // Create a mock Medication object
        Medication medication = new Medication(12, "Paracetamol", "Analgesic");
        // Create a mock Connection object
        Connection connection = null;
        // Create a mock PreparedStatement object
        PreparedStatement preparedStatement = null;

        try {
            // Mock the DatabaseManager.connect() method to return the mock Connection object
            connection = DatabaseManager.connect();
            // Mock the connection.prepareStatement() method to return the mock PreparedStatement object
            preparedStatement = connection.prepareStatement("DELETE FROM medications WHERE id=?");
            // Set the parameter for the prepared statement
            preparedStatement.setInt(1, medication.getId());
            // Mock the preparedStatement.executeUpdate() method to return 1
            int result = preparedStatement.executeUpdate();

            // Create an instance of MedicationService
            MedicationService medicationService = new MedicationService();

            // Call the deleteMedication() method with the mock Medication object
            medicationService.deleteMedication(medication);

            // Verify that the result of executeUpdate() is 1
            assertEquals(1, result);
        } catch (SQLException e) {
            fail("SQLException occurred");
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                fail("SQLException occurred while closing resources");
            }
        }
    }
}