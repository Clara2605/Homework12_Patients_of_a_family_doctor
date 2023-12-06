package com.ace.ucv.repositories;

import com.ace.ucv.model.Disease;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        // This test assumes that addDisease works correctly without verification
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

    @Test
    void testDeleteDisease() {
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        Disease diseaseToDelete = diseases.stream()
                .filter(d -> d.getName().equals(EDITED_DISEASE_NAME))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Disease not found for deletion"));

        diseaseRepository.deleteDisease(diseaseToDelete);

        diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertFalse(diseases.stream().anyMatch(d -> d.getName().equals(EDITED_DISEASE_NAME)));
    }
}
