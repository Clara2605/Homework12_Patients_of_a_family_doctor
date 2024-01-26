package com.ace.ucv.repositories;
import com.ace.ucv.model.Medication;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class MedicationRepositoryTest {
    @Test
    void test_addMedication() {
        MedicationRepository medicationRepository = new MedicationRepository();
        Medication medication = new Medication("Medication 1", "Category 1");
        medicationRepository.addMedication(medication); List<Medication> medications =
                medicationRepository.loadMedicationsFromDatabase();
        Medication addedMedication = medications.get(medications.size() - 1);
        assertEquals(medication.getName(), addedMedication.getName());
        assertEquals(medication.getCategory(), addedMedication.getCategory());
    }
    @Test
    void test_editMedication() {
        MedicationRepository medicationRepository = new MedicationRepository();
        Medication medication = new Medication("Medication 1", "Category 1");
        medicationRepository.addMedication(medication); String editedName = "Edited Medication"; String editedCategory = "Edited Category";
        medicationRepository.editMedication(medication, editedName, editedCategory);
        List<Medication> medications = medicationRepository.loadMedicationsFromDatabase();
        Medication editedMedication = medications.get(medications.size() - 1);
        assertEquals(editedName, editedMedication.getName());
        assertEquals(editedCategory, editedMedication.getCategory());
    }
    @Test
    void test_deleteMedication() {
        MedicationRepository medicationRepository = new MedicationRepository();
        Medication medication = new Medication("Medication 1", "Category 1");
        medicationRepository.addMedication(medication);
        medicationRepository.deleteMedication(medication);
        List<Medication> medications = medicationRepository.loadMedicationsFromDatabase();
        assertFalse(medications.contains(medication));
    }
}