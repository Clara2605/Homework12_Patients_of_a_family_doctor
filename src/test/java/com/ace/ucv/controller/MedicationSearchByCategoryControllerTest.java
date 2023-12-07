package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.model.Medication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicationSearchByCategoryControllerTest {

    @Test
    public void test_empty_observable_list() {
        MedicationSearchByCategoryController controller = new MedicationSearchByCategoryController();
        ObservableList<Medication> result = controller.getMedicationsByCategoryWithCount("category");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testGetMedicationsByCategoryWithCount() {
        MedicationSearchByCategoryController controller = new MedicationSearchByCategoryController();

        String category = "NonExistentCategory";
        ObservableList<Medication> medications = controller.getMedicationsByCategoryWithCount(category);

        // Verificați că lista de medicamente este goală pentru o categorie inexistentă
        assertTrue(medications.isEmpty());
    }
}