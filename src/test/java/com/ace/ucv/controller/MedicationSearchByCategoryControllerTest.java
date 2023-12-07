package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void testValidCategoryReturnsNonEmptyList() {
        MedicationSearchByCategoryController controller = new MedicationSearchByCategoryController();
        String existingCategory = "Analgesic"; // Înlocuiește cu o categorie validă din baza ta de date de test
        ObservableList<Medication> medications = controller.getMedicationsByCategoryWithCount(existingCategory);

        assertFalse(medications.isEmpty(), "Lista de medicamente nu ar trebui să fie goală pentru o categorie existentă");
        for (Medication medication : medications) {
            assertEquals(existingCategory, medication.getCategory(), "Categoria medicamentului nu corespunde cu categoria interogată");
        }
    }

}