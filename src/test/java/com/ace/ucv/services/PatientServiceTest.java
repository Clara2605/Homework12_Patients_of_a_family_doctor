//package com.ace.ucv.services;
//
//import com.ace.ucv.model.Patient;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PatientServiceTest {
//    @Test
//    public void test_addPatient_validData() {
//        // Arrange
//        PatientService patientService = new PatientService();
//        Patient patient = new Patient("John Doe", 30, "Doctor");
//
//        // Act
//        patientService.addPatient(patient);
//
//        // Assert
//        assertNotNull(patient.getId());
//    }
//
//    @Test
//    public void test_loadPatientsFromDatabase() {
//        // Arrange
//        PatientService patientService = new PatientService();
//
//        // Act
//        List<Patient> patients = patientService.loadPatientsFromDatabase();
//
//        // Assert
//        assertNotNull(patients);
//        assertFalse(patients.isEmpty());
//    }
//
//    @Test
//    public void test_deletePatient_validData() {
//        // Arrange
//        PatientService patientService = new PatientService();
//        Patient patient = new Patient(21, "John Doe", 30, "Doctor");
//
//        // Act
//        patientService.deletePatient(patient);
//
//        // Assert
//        // Verify that the patient is no longer in the database
//        List<Patient> patients = patientService.loadPatientsFromDatabase();
//        assertFalse(patients.contains(patient));
//    }
//
//    @Test
//    public void test_editPatient_validData() {
//        // Arrange
//        PatientService patientService = new PatientService();
//        Patient patient = new Patient(23, "John Doe", 30, "Doctor");
//        String newName = "Jane Smith";
//        int newAge = 35;
//        String newFieldOfWork = "Nurse";
//
//        // Act
//        patientService.editPatient(patient, newName, newAge, newFieldOfWork);
//
//        // Assert
//        assertEquals(newName, patient.getName());
//        assertEquals(newAge, patient.getAge());
//        assertEquals(newFieldOfWork, patient.getFieldOfWork());
//    }
//}