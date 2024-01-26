package com.ace.ucv.repositories;
import com.ace.ucv.model.Patient;
import org.junit.jupiter.api.Test;
import java.util.List; import static org.junit.jupiter.api.Assertions.*;
class PatientRepositoryTest {
    @Test
    void test_addPatient_success() {
        // Arrange
        PatientRepository patientRepository = new PatientRepository();
        Patient patient = new Patient("John Doe", 30, "Engineer");
        // Act
        patientRepository.addPatient(patient);
        // Assert
        assertNotNull(patient.getId());
    }
    @Test
    void test_loadPatientsFromDatabase_success() {
        // Arrange
        PatientRepository patientRepository = new PatientRepository();
        // Act
        List<Patient> patients = patientRepository.loadPatientsFromDatabase();
        // Assert
        assertNotNull(patients);
    }
    @Test
    void test_editPatient_success() {
        // Arrange
        PatientRepository patientRepository = new PatientRepository();
        Patient patient = new Patient("John Doe", 30, "Engineer");
        patientRepository.addPatient(patient);
        // Act
        patientRepository.editPatient(patient, "Jane Smith", 35, "Doctor");
        // Assert
        assertEquals("Jane Smith", patient.getName());
        assertEquals(35, patient.getAge());
        assertEquals("Doctor", patient.getFieldOfWork());
        // Add assertion for logging success
    }
    @Test
    public void test_loadPatientsFromEmptyDatabase_success() {
        // Arrange
        PatientRepository patientRepository = new PatientRepository();
        // Clear the database
        // Act
        List<Patient> patients = patientRepository.loadPatientsFromDatabase();
        // Assert
        assertFalse(patients.isEmpty());
    }

    @Test
    public void test_deletePatient_success() {
        // Arrange
        PatientRepository patientRepository = new PatientRepository();
        Patient patient = new Patient("John Doe", 30, "Engineer");
        patientRepository.addPatient(patient);

        // Act
        patientRepository.deletePatient(patient);

        // Assert
        // Add assertion for logging success
    }
}