package com.ace.ucv.controller;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicationSearchController {

    private static final Logger logger = LoggerFactory.getLogger(MedicationSearchController.class);
    private ObservableList<Patient> data = FXCollections.observableArrayList();

    public Pair<ObservableList<Patient>, Integer> performSearch(String medicationName) {
        List<Patient> patients = getPatientsWithMedication(medicationName);
        data.setAll(patients);
        return new Pair<>(data, patients.size());
    }

    private List<Patient> getPatientsWithMedication(String medicationName) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.*, m.name as medication_name FROM patients p " +
                "JOIN prescriptions pr ON p.id = pr.patient_id " +
                "JOIN medications m ON m.id = pr.medication_id " +
                "WHERE m.name = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicationName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("field_of_work"),
                            rs.getString("medication_name"),
                            false
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting patients with medication: " + medicationName, e);
            throw new RuntimeException("Error getting patients with medication: " + e.getMessage(), e);
        }
        return patients;
    }
}
