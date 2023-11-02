package com.ace.ucv.controller;

import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

}