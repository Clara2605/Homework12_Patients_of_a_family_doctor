package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DiseaseRepositoryTest {
    private static final String INITIAL_DISEASE_NAME = "Initial Disease";
    private static final String EDITED_DISEASE_NAME = "Edited Disease";
    private DiseaseRepository diseaseRepository;

    @BeforeEach
    public void setUp() {
        diseaseRepository = new DiseaseRepository();
    }

    @Test
    void testAddDisease() {
        diseaseRepository.addDisease(INITIAL_DISEASE_NAME);
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertTrue(diseases.stream().anyMatch(d -> d.getName().equals(INITIAL_DISEASE_NAME)));
    }

    @Test
    void testEditDisease() {
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        Disease diseaseToEdit = diseases.stream()
                .filter(d -> d.getName().equals(INITIAL_DISEASE_NAME))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Disease not found for editing"));
        diseaseRepository.editDisease(diseaseToEdit, EDITED_DISEASE_NAME);
        diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertTrue(diseases.stream().anyMatch(d -> d.getName().equals(EDITED_DISEASE_NAME)));
    }

//    @Test
//    void testDeleteDisease() {
//        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
//        Disease diseaseToDelete = diseases.stream()
//                .filter(d -> d.getName().equals(EDITED_DISEASE_NAME))
//                .findFirst()
//                .orElseThrow(() -> new AssertionError("Disease not found for deletion"));
//        diseaseRepository.deleteDisease(diseaseToDelete);
//        diseases = diseaseRepository.loadDiseasesFromDatabase();
//        assertFalse(diseases.stream().anyMatch(d -> d.getName().equals(EDITED_DISEASE_NAME)));
//    }

    @Test
    void test_addDisease_addsNewDiseaseToDatabase() {
        String diseaseName = "Test Disease";
        diseaseRepository.addDisease(diseaseName);
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertTrue(diseases.stream().anyMatch(d -> d.getName().equals(diseaseName)));
    }

//    @Test
//    void test_loadDiseasesFromDatabase_returnsEmptyListIfNoDiseasesInDatabase() {
//        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
//        assertTrue(diseases.isEmpty());
//    }
//
//    @Test
//    void test_loadDiseasesFromDatabase_returnsListOfAllDiseases() {
//        // Arrange
//        DiseaseRepository diseaseRepository = new DiseaseRepository();
//        String diseaseName1 = "Test Disease 1";
//        String diseaseName2 = "Test Disease 2";
//        diseaseRepository.addDisease(diseaseName1);
//        diseaseRepository.addDisease(diseaseName2);
//
//        // Act
//        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
//
//        // Assert
//        assertEquals(diseaseName1, diseases.get(0).getName());
//        assertEquals(diseaseName2, diseases.get(1).getName());
//    }

    @Test
    void test_editDisease_updatesNameInDatabaseAndObject() {
        // Arrange
        DiseaseRepository diseaseRepository = new DiseaseRepository();
        String diseaseName = "Test Disease";
        String editedName = "Edited Disease";
        diseaseRepository.addDisease(diseaseName);
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        Disease disease = diseases.get(0);

        // Act
        diseaseRepository.editDisease(disease, editedName);

        // Assert
        diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertEquals(editedName, diseases.get(0).getName());
        assertEquals(editedName, disease.getName());
    }
}
