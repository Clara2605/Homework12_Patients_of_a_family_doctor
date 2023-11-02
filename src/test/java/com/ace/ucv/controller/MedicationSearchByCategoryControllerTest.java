package com.ace.ucv.controller;

import com.ace.ucv.model.Medication;
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
}