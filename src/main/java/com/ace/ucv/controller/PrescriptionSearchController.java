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

public class PrescriptionSearchController implements IPrescriptionSearch {

    private static final Logger logger = LogManager.getLogger(PrescriptionSearchController.class);
    private static final String GET_PATIENTS_WITH_PRESCRIPTION_COUNT_SQL =
            "SELECT p.*, (COUNT(pr.id) / (1 + (julianday(MAX(pr.date)) - julianday(MIN(pr.date))) / 30.44)) as prescriptions_per_month " +
                    "FROM patients p JOIN prescriptions pr ON p.id = pr.patient_id " +
                    "WHERE pr.date >= date('now', '-1 month') " + // Consider prescriptions within the last month
                    "GROUP BY p.id " +
                    "HAVING prescriptions_per_month > ?";

    public ObservableList<Patient> getPatientsWithPrescriptionCount(int minPrescriptions) {
        ObservableList<Patient> patients = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(GET_PATIENTS_WITH_PRESCRIPTION_COUNT_SQL)) {

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
