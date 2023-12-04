package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IPrescriptionSearch;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionSearchController implements IPrescriptionSearch {

    private static final Logger logger = LogManager.getLogger(PrescriptionSearchController.class);

    public ObservableList<Patient> getPatientsWithPrescriptionCount(int minPrescriptions) {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String sql = "SELECT p.*, COUNT(pr.id) as prescription_count " +
                "FROM patients p JOIN prescriptions pr ON p.id = pr.patient_id " +
                "GROUP BY p.id " +
                "HAVING COUNT(pr.id) > ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, minPrescriptions);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("field_of_work")
                            // Include other necessary fields
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting patients with a minimum %d of prescriptions %s ", minPrescriptions, e.getMessage()));
            throw new RuntimeException(String.format("Database error occurred: %s", e.getMessage()));
        }

        return patients;
    }
}
