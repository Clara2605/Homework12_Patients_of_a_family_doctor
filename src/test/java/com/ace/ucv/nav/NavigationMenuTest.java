package com.ace.ucv.nav;

import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NavigationMenuTest {
    @Test
    public void test_emptyObservableLists() {
        // Arrange
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        ObservableList<Disease> diseases = FXCollections.observableArrayList();
        ObservableList<Medication> medications = FXCollections.observableArrayList();

        // Assert
        assertTrue(patients.isEmpty());
        assertTrue(diseases.isEmpty());
        assertTrue(medications.isEmpty());
    }

}