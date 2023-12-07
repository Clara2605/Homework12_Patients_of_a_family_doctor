package com.ace.ucv.controller;

import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiseaseSearchControllerTest {

    @Test
    public void test_returns_pair_object_with_observable_list_and_number_of_patients() {
        DiseaseSearchController controller = new DiseaseSearchController();
        Pair<ObservableList<Patient>, Integer> result = controller.performSearch("diseaseName");
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getValue());
        assertTrue(result.getKey() instanceof ObservableList);
        assertTrue(result.getValue() instanceof Integer);
    }
    @Test
    public void test_get_patients_with_disease_returns_list_of_patients() {
        // Arrange
        DiseaseSearchController controller = new DiseaseSearchController();
        String diseaseName = "COVID";

        // Act
        List<Patient> patients = controller.getPatientsWithDisease(diseaseName);

        // Assert
        assertNotNull(patients);
        assertEquals(0, patients.size());
    }

    @Test
    public void test_handles_null_input() {
        // Arrange
        DiseaseSearchController controller = new DiseaseSearchController();

        // Act
        Pair<ObservableList<Patient>, Integer> result = controller.performSearch(null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getValue());
        assertEquals(0, result.getKey().size());
        assertEquals(0, result.getValue().intValue());
    }
    @Test
    public void test_perform_search_with_empty_disease_name() {
        // Arrange
        DiseaseSearchController controller = new DiseaseSearchController();
        String diseaseName = "";

        // Act
        Pair<ObservableList<Patient>, Integer> result = controller.performSearch(diseaseName);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getValue());
        assertEquals(0, result.getKey().size());
        assertEquals(0, result.getValue().intValue());
    }

}