package com.ace.ucv.repositories;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionRepositoryTest {
    @Test
    public void test_save_prescription() {
        // Arrange
        PrescriptionRepository prescriptionRepository = new PrescriptionRepository();
        Patient patient = new Patient("John Doe", 30, "Engineer");
        String date = "2021-01-01";
        String diseaseId = "1";
        String medicationId = "1";

        // Act
        boolean result = prescriptionRepository.savePrescription(patient, date, diseaseId, medicationId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void test_load_prescriptions_from_database() {
        // Arrange
        PrescriptionRepository prescriptionRepository = new PrescriptionRepository();

        // Act
        ObservableList<Prescription> prescriptions = prescriptionRepository.loadPrescriptionsFromDatabase();

        // Assert
        assertNotNull(prescriptions);
        //assertEquals(1, prescriptions.size());
    }

    @Test
    public void test_edit_prescription() {
        // Arrange
        PrescriptionRepository prescriptionRepository = new PrescriptionRepository();
        int id = 40;
        String date = "2021-01-01";
        int patientId = 0;
        int diseaseId = 5;
        int medicationId = 1;

        // Act
        boolean result = prescriptionRepository.editPrescription(id, date, patientId, diseaseId, medicationId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void test_delete_last_prescription() {
        // Arrange
        PrescriptionRepository prescriptionRepository = new PrescriptionRepository();

        // Retrieve the last ID from the database
        int lastId = prescriptionRepository.getLastPrescriptionId();

        // Act
        boolean result = prescriptionRepository.deletePrescription(lastId);

        // Assert
        assertTrue(result);
    }
}