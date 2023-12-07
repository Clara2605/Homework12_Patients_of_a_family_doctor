package com.ace.ucv.controller;

import com.ace.ucv.classification.MedicationSearchByCategory;
import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MedicationSearchControllerTest {

    @Test
    public void test_perform_search_returns_pair_object() {
        MedicationSearchController controller = new MedicationSearchController();
        Pair<ObservableList<Patient>, Integer> result = controller.performSearch("medicationName");
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getValue());
        assertTrue(result.getKey() instanceof ObservableList);
        assertTrue(result.getValue() instanceof Integer);
    }

    @Test
    public void test_valid_medication_category() {
        // Arrange
        IMedicationSearchByCategory mockController = mock(IMedicationSearchByCategory.class);
        String validCategory = "Analgesic";
        ObservableList<Medication> expectedMedications = FXCollections.observableArrayList(new Medication(17, "Paracetamol", validCategory, 3), new Medication(2, "Medication 2", validCategory, 5));
        when(mockController.getMedicationsByCategoryWithCount(validCategory)).thenReturn(expectedMedications);

        // Act
        ObservableList<Medication> actualMedications = mockController.getMedicationsByCategoryWithCount(validCategory);

        // Assert
        assertEquals(expectedMedications, actualMedications);
    }
}