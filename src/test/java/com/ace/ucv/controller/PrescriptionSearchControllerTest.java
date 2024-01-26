package com.ace.ucv.controller;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionSearchControllerTest {
    @Test
    public void test_retrieve_patients_with_min_prescriptions() {
        // Arrange
        PrescriptionSearchController controller = new PrescriptionSearchController();
        int minPrescriptions = 1;

        // Act
        ObservableList<Patient> patients = controller.getPatientsWithPrescriptionCount(minPrescriptions);

        // Assert
        assertNotNull(patients);
        assertEquals(1, patients.size());
    }

    @Test
    public void test_min_prescriptions_zero() {
        // Arrange
        PrescriptionSearchController controller = new PrescriptionSearchController();
        int minPrescriptions = 0;

        // Act
        ObservableList<Patient> patients = controller.getPatientsWithPrescriptionCount(minPrescriptions);

        // Assert
        assertNotNull(patients);
        assertEquals(3, patients.size());
    }
    @Test
    public void test_database_connection_successful() {

        // Act
        Connection conn = DatabaseManager.connect();

        // Assert
        assertNotNull(conn);
    }
    @Test
    public void test_sql_query_execution() {
        // Arrange
        PrescriptionSearchController controller = new PrescriptionSearchController();
        int minPrescriptions = 1;

        // Act
        ObservableList<Patient> patients = controller.getPatientsWithPrescriptionCount(minPrescriptions);

        // Assert
        assertNotNull(patients);
    }
}