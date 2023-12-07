package com.ace.ucv.services;

import com.ace.ucv.model.Disease;
import com.ace.ucv.repositories.DiseaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiseaseServiceTest {

    private DiseaseService diseaseService;
    private DiseaseRepositoryStub diseaseRepositoryStub;

    @BeforeEach
    void setUp() {
        diseaseRepositoryStub = new DiseaseRepositoryStub();
        diseaseService = new DiseaseService(diseaseRepositoryStub);
    }

    @Test
    void test_addDisease() {
        DiseaseService diseaseService = new DiseaseService();
        diseaseService.addDisease("Flu");
        List<Disease> diseases = diseaseService.loadDiseasesFromDatabase();
        boolean found = false;
        for (Disease disease : diseases) {
            if (disease.getName().equals("Flu")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void test_loadDiseasesFromDatabase() {
        DiseaseService diseaseService = new DiseaseService();
        List<Disease> diseases = diseaseService.loadDiseasesFromDatabase();
        assertNotNull(diseases);
    }

    @Test
    void test_editDisease() {
        DiseaseService diseaseService = new DiseaseService();
        List<Disease> diseases = diseaseService.loadDiseasesFromDatabase();
        if (!diseases.isEmpty()) {
            Disease disease = diseases.get(0);
            String editedName = "Edited Disease";
            diseaseService.editDisease(disease, editedName);
            List<Disease> updatedDiseases = diseaseService.loadDiseasesFromDatabase();
            boolean found = false;
            for (Disease updatedDisease : updatedDiseases) {
                if (updatedDisease.getName().equals(editedName)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
    @Test
    void deleteDiseaseTest() {
        Disease disease = new Disease(); // Assume Disease has a default constructor
        // Add necessary details to the disease object if required

        diseaseService.deleteDisease(disease);

        // Verify that the disease was deleted from the stub repository
        assert !diseaseRepositoryStub.diseases.contains(disease);
    }

    private static class DiseaseRepositoryStub extends DiseaseRepository {
        List<Disease> diseases = new ArrayList<>();

        @Override
        public void deleteDisease(Disease disease) {
            diseases.remove(disease);
        }

        // Implement other methods as necessary for the stub
    }

}
