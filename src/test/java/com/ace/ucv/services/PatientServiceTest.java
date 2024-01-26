package com.ace.ucv.services;

import com.ace.ucv.model.Patient;
import com.ace.ucv.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        patientService = new PatientService(patientRepository);
    }

    @Test
    void test_loadPatientsFromDatabase_whenRepositoryReturnsPatients_thenReturnPatients() {
        // Arrange
        List<Patient> expectedPatients = new ArrayList<>();
        expectedPatients.add(new Patient(1, "John Doe", 30, "Doctor"));
        expectedPatients.add(new Patient(2, "Jane Smith", 25, "Nurse"));
        when(patientRepository.loadPatientsFromDatabase()).thenReturn(expectedPatients);

        // Act
        List<Patient> actualPatients = patientService.loadPatientsFromDatabase();

        // Assert
        assertEquals(expectedPatients, actualPatients);
    }

    @Test
    void test_loadPatientsFromDatabase_whenRepositoryReturnsEmptyList_thenReturnEmptyList() {
        // Arrange
        List<Patient> expectedPatients = new ArrayList<>();
        when(patientRepository.loadPatientsFromDatabase()).thenReturn(expectedPatients);

        // Act
        List<Patient> actualPatients = patientService.loadPatientsFromDatabase();

        // Assert
        assertTrue(actualPatients.isEmpty());
    }

    @Test
    void test_addPatient_thenRepositoryAddPatient() {
        // Arrange
        Patient patient = new Patient("Alice", 28, "Engineer");

        // Act
        patientService.addPatient(patient);

        // Assert
        verify(patientRepository, times(1)).addPatient(patient);
    }

    @Test
    void test_editPatient_thenRepositoryEditPatient() {
        // Arrange
        Patient patient = new Patient(1, "Bob", 40, "Teacher");
        String newName = "Robert";
        int newAge = 41;
        String newFieldOfWork = "Principal";

        // Act
        patientService.editPatient(patient, newName, newAge, newFieldOfWork);

        // Assert
        verify(patientRepository, times(1)).editPatient(patient, newName, newAge, newFieldOfWork);
    }

    @Test
    void test_deletePatient_thenRepositoryDeletePatient() {
        // Arrange
        Patient patient = new Patient(1, "Charlie", 35, "Pilot");

        // Act
        patientService.deletePatient(patient);

        // Assert
        verify(patientRepository, times(1)).deletePatient(patient);
    }
}
