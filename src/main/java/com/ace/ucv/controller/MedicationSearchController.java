package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IMedicationSearch;
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

public class MedicationSearchController implements IMedicationSearch {

    private static final Logger logger = LogManager.getLogger(MedicationSearchController.class);
    private static final String GET_PATIENTS_WITH_MEDICATION_SQL = "SELECT p.*, m.name as medication_name FROM patients p " +
            "JOIN prescriptions pr ON p.id = pr.patient_id " +
            "JOIN medications m ON m.id = pr.medication_id " +
            "WHERE m.name = ?";
    private ObservableList<Patient> data = FXCollections.observableArrayList();

    public Pair<ObservableList<Patient>, Integer> performSearch(String medicationName) {
        List<Patient> patients = getPatientsWithMedication(medicationName);
        data.setAll(patients);
        return new Pair<>(data, patients.size());
    }

    public List<Patient> getPatientsWithMedication(String medicationName) {
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(GET_PATIENTS_WITH_MEDICATION_SQL)) {
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
            logger.error(String.format("Error getting patients with medication: %s due to: %s", medicationName, e.getMessage()));
            throw new RuntimeException("Error getting patients with medication: " + e.getMessage());
        }
        return patients;
    }
}
