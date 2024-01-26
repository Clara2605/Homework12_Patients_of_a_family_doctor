package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicationServiceTest {
    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        this.medicationService = new MedicationService();
    }

    @Test
    void test_editMedication_successfullyUpdatesDatabase() {
        // Arrange
        Medication medication = new Medication("Medication 1", "Category 1");
        medicationService.addMedication(medication);

        // Act
        String editedName = "Edited Medication 1";
        String editedCategory = "Edited Category 1";
        medicationService.editMedication(medication, editedName, editedCategory);

        // Assert
        List<Medication> medications = medicationService.loadMedicationsFromDatabase();
        Medication editedMedication = medications.stream()
                .filter(m -> m.getId() == medication.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(editedMedication);
        assertEquals(editedName, editedMedication.getName());
        assertEquals(editedCategory, editedMedication.getCategory());
    }

    @Test
    void test_deleteMedication_successfullyUpdatesDatabase() {
        // Arrange
        Medication medication = new Medication("Medication 1", "Category 1");
        medicationService.addMedication(medication);

        // Act
        medicationService.deleteMedication(medication);

        // Assert
        List<Medication> medications = medicationService.loadMedicationsFromDatabase();
        assertFalse(medications.contains(medication));
    }
}
