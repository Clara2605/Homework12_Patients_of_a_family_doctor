package com.ace.ucv.repositories;

import com.ace.ucv.model.Disease;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiseaseRepositoryTest {
    private DiseaseRepository diseaseRepository;

    @BeforeEach
    public void setUp() {
        diseaseRepository = new DiseaseRepository();
    }

    @Test
    void testAddDisease() {
        String initialDiseaseName = "Initial Disease";
        diseaseRepository.addDisease(initialDiseaseName);
        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertTrue(diseases.stream().anyMatch(d -> d.getName().equals(initialDiseaseName)));
    }

    @Test
    void testEditDisease() {
        String initialDiseaseName = "Initial Disease";
        String editedDiseaseName = "Edited Disease";

        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        Disease diseaseToEdit = diseases.stream()
                .filter(d -> d.getName().equals(initialDiseaseName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Disease not found"));

        diseaseRepository.editDisease(diseaseToEdit, editedDiseaseName);
    }

    @Test
    void testDeleteDisease() {
        String editedDiseaseName = "Edited Disease";

        List<Disease> diseases = diseaseRepository.loadDiseasesFromDatabase();
        Disease diseaseToDelete = diseases.stream()
                .filter(d -> d.getName().equals(editedDiseaseName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Disease not found"));

        diseaseRepository.deleteDisease(diseaseToDelete);

        diseases = diseaseRepository.loadDiseasesFromDatabase();
        assertFalse(diseases.stream().anyMatch(d -> d.getName().equals(editedDiseaseName)));
    }
}
