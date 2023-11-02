package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Prescription;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionServiceTest {
    @Test
    public void test_loadPrescriptionsFromDatabase() {
        PrescriptionService prescriptionService = new PrescriptionService();

        // Call the method under test
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();

        // Assert the result
        assertEquals(5, prescriptions.size());
        Prescription prescription = prescriptions.get(0);
        assertEquals(12, prescription.getId());
        assertEquals("2023-05-31", prescription.getDate());
        assertEquals("Faringita Acuta", prescription.getDisease());
        assertEquals("Diclofenac", prescription.getMedication());
    }

    @Test
    public void test_editPrescription_updatesPrescription() throws SQLException {
        // Create a mock Connection object
        Connection connection = DatabaseManager.connect();

        // Create a mock PreparedStatement object
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prescriptions SET date = ?, patient_id = ?, disease_id = ?, medication_id = ? WHERE id = ?");

        // Create a mock PrescriptionService object
        PrescriptionService prescriptionService = new PrescriptionService();

        // Set up the mock objects
        int id = 1;
        String date = "2021-01-01";
        int patientId = 1;
        int diseaseId = 1;
        int medicationId = 1;

        // Call the method under test
        boolean result = prescriptionService.editPrescription(id, date, patientId, diseaseId, medicationId);

        // Assert the result
        assertTrue(result);
    }

    @Test
    public void test_deletePrescription_deletesPrescription() throws SQLException {
        // Create a mock Connection object
        Connection connection = DatabaseManager.connect();

        // Create a mock PreparedStatement object
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM prescriptions WHERE id = ?");

        // Create a mock PrescriptionService object
        PrescriptionService prescriptionService = new PrescriptionService();

        // Set up the mock objects
        int id = 1;

        // Call the method under test
        boolean result = prescriptionService.deletePrescription(id);

        // Assert the result
        assertTrue(result);
    }
}