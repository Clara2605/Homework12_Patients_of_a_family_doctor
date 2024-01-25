package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IDiseaseSearch;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiseaseSearchController implements IDiseaseSearch {

    private static final Logger logger = LogManager.getLogger(DiseaseSearchController.class);
    private static final String GET_PATIENTS_WITH_DISEASE_SQL = "SELECT p.*, d.name as disease_name FROM patients p " +
            "JOIN prescriptions pr ON p.id = pr.patient_id " +
            "JOIN diseases d ON d.id = pr.disease_id " +
            "WHERE d.name = ?";
    private ObservableList<Patient> data = FXCollections.observableArrayList();

    /**
     * Performs a search for patients with a specified disease and returns the results.
     * The results are returned as a pair containing an ObservableList of patients and the size of the list.
     *
     * @param diseaseName The name of the disease to search for.
     * @return A Pair containing an ObservableList of Patients and an Integer representing the number of patients found.
     */
    public Pair<ObservableList<Patient>, Integer> performSearch(String diseaseName) {
        List<Patient> patients = getPatientsWithDisease(diseaseName);
        data.setAll(patients);
        return new Pair<>(data, patients.size());
    }

    // Retrieves a list of patients diagnosed with a specific disease from the database.
     public List<Patient> getPatientsWithDisease(String diseaseName) {
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(GET_PATIENTS_WITH_DISEASE_SQL)) {
            pstmt.setString(1, diseaseName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("field_of_work"),
                            rs.getString("disease_name"),
                            true
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error fetching patients with disease: %s due to: %s", diseaseName, e.getMessage()));
            throw new RuntimeException(String.format("Error fetching patients with disease: %s", e.getMessage()));
        }
        return patients;
    }
}
