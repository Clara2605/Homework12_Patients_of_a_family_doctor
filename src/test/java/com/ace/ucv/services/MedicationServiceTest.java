//package com.ace.ucv.services;
//
//import com.ace.ucv.db.DatabaseManager;
//import com.ace.ucv.model.Medication;
//import org.junit.jupiter.api.Test;
//
//import java.sql.*;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MedicationServiceTest {
//
//    @Test
//    void editMedication() {
//        MedicationService medicationService = new MedicationService();
//        Medication medication = new Medication("Paracetamol", "Analgesic");
//        medicationService.addMedication(medication);
//        medicationService.editMedication(medication, "Paracetamol", "Analgesic");
//        assertEquals("Paracetamol", medication.getName());
//        assertEquals("Analgesic", medication.getCategory());
//    }
//
//    @Test
//    public void test_loadMedicationsFromDatabase_successfullyReturnsListOfMedicationObjects() {
//        // Create a mock Connection object
//        Connection connection = null;
//        // Create a mock Statement object
//        Statement statement = null;
//        // Create a mock ResultSet object
//        ResultSet resultSet = null;
//
//        try {
//
//            connection = DatabaseManager.connect();
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery("SELECT * FROM medications");
//             boolean hasNext = resultSet.next();
//            int id = resultSet.getInt("id");
//            String name = resultSet.getString("name");
//            String category = resultSet.getString("category");
//
//            MedicationService medicationService = new MedicationService();
//            List<Medication> medications = medicationService.loadMedicationsFromDatabase();
//            assertEquals(10, medications.size());
//            Medication medication = medications.get(0);
//            assertEquals(id, medication.getId());
//            assertEquals(name, medication.getName());
//            assertEquals(category, medication.getCategory());
//        } catch (SQLException e) {
//            fail("SQLException occurred");
//        } finally {
//            try {
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//                if (statement != null) {
//                    statement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                fail("SQLException occurred while closing resources");
//            }
//        }
//    }
//
//    @Test
//    public void test_deleteMedication_successfullyRemovesMedicationFromDatabase() {
//        // Create a mock Medication object
//        Medication medication = new Medication(12, "Paracetamol", "Analgesic");
//        // Create a mock Connection object
//        Connection connection = null;
//        // Create a mock PreparedStatement object
//        PreparedStatement preparedStatement = null;
//
//        try {
//            connection = DatabaseManager.connect();
//            preparedStatement = connection.prepareStatement("DELETE FROM medications WHERE id=?");
//            // Set the parameter for the prepared statement
//            preparedStatement.setInt(1, medication.getId());
//            int result = preparedStatement.executeUpdate();
//
//            MedicationService medicationService = new MedicationService();
//            medicationService.deleteMedication(medication);
//
//            assertEquals(1, result);
//        } catch (SQLException e) {
//            fail("SQLException occurred");
//        } finally {
//            try {
//                if (preparedStatement != null) {
//                    preparedStatement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                fail("SQLException occurred while closing resources");
//            }
//        }
//    }
//}