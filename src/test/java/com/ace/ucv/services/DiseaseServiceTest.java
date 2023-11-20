//package com.ace.ucv.services;
//
//import com.ace.ucv.model.Disease;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DiseaseServiceTest {
//    @Test
//    public void test_addDisease_success() {
//        // Arrange
//        DiseaseService diseaseService = new DiseaseService();
//        String diseaseName = "Sinuzita";
//
//        // Act
//        diseaseService.addDisease(diseaseName);
//        List<Disease> diseases = diseaseService.loadDiseasesFromDatabase();
//
//        // Assert
//        assertTrue(diseases.stream().anyMatch(d -> d.getName().equals(diseaseName)));
//    }
//
//    @Test
//    public void test_loadDiseasesFromDatabase_success() {
//        // Arrange
//        DiseaseService diseaseService = new DiseaseService();
//
//        // Act
//        List<Disease> diseases = diseaseService.loadDiseasesFromDatabase();
//
//        // Assert
//        assertFalse(diseases.isEmpty());
//    }
//
//
//}