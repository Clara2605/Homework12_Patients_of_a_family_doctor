package com.ace.ucv.repositories;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    public void test_create_prescription_with_valid_parameters() {
        // Arrange
        int id = 1;
        String date = "2021-01-01";
        String disease = "Flu";
        String medication = "Paracetamol";

        // Act
        Prescription prescription = new Prescription(id, date, disease, medication);

        // Assert
        assertNotNull(prescription);
        assertEquals(id, prescription.getId());
        assertEquals(date, prescription.getDate());
        assertEquals(disease, prescription.getDisease());
        assertEquals(medication, prescription.getMedication());
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

        // Assuming you have a method to add a prescription and retrieve its ID
        int id = addTestPrescription(prescriptionRepository);

        // New details for the prescription
        String newDate = "2022-01-01";
        int newPatientId = 10; // Use valid IDs that exist in your test database
        int newDiseaseId = 143;
        int newMedicationId = 3;

        // Act
        boolean result = prescriptionRepository.editPrescription(id, newDate, newPatientId, newDiseaseId, newMedicationId);

        // Assert
        assertTrue(result, "Editing prescription should return true");
    }

    private int addTestPrescription(PrescriptionRepository prescriptionRepository) {
        // Add a test prescription to the database and return its ID
        // Use valid patient, disease, and medication IDs
        // This method should mimic the actual adding of a prescription
        // For example:
        String date = "2021-01-01";
        int patientId = 10; // Assuming this ID exists
        int diseaseId = 143; // Assuming this ID exists
        int medicationId = 3; // Assuming this ID exists

        // Add the prescription
        prescriptionRepository.savePrescription(new Patient("Test", 25, "Test Profession"), date, String.valueOf(diseaseId), String.valueOf(medicationId));

        // Retrieve and return the last added prescription ID
        return prescriptionRepository.getLastPrescriptionId();
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

    @Test
    public void testGetIdFromNameWithValidName() {
        PrescriptionRepository repository = new PrescriptionRepository();
        String validName = "Paracetamol"; // Înlocuiește cu un nume valid din baza de date
        String tableName = "medications"; // Înlocuiește cu numele tabelului corespunzător

        int id = repository.getIdFromName(tableName, validName);

        assertTrue(id > 0, "ID-ul ar trebui să fie pozitiv pentru un nume valid.");
    }

    @Test
    public void testLoadItemsFromDatabase() {
        PrescriptionRepository repository = new PrescriptionRepository();
        String tableName = "medications"; // Înlocuiește cu numele unui tabel valid
        String columnName = "Name"; // Înlocuiește cu numele unei coloane valide din tabel

        List<String> items = repository.loadItemsFromDatabase(tableName, columnName);

        assertNotNull(items, "Lista de elemente nu ar trebui să fie null.");
        // Poți adăuga mai multe verificări pentru a te asigura că elementele sunt corecte.
    }

    @Test
    public void test_retrieve_disease_id() {
        // Arrange
        int id = 1;
        String date = "2021-01-01";
        String disease = "Flu";
        String medication = "Paracetamol";
        int diseaseId = 123;
        int medicationId = 456;
        Prescription prescription = new Prescription(id, date, disease, medication, diseaseId, medicationId);

        // Act
        int retrievedDiseaseId = prescription.getDiseaseId();

        // Assert
        assertEquals(diseaseId, retrievedDiseaseId);
    }

    @Test
    public void test_retrieve_medication_id() {
        // Arrange
        int id = 1;
        String date = "2021-01-01";
        String disease = "Flu";
        String medication = "Paracetamol";
        int medicationId = 0;

        Prescription prescription = new Prescription(id, date, disease, medication);
       // prescription.setMedicationId(medicationId);

        // Act
        int retrievedMedicationId = prescription.getMedicationId();

        // Assert
        assertEquals(medicationId, retrievedMedicationId);
    }

}