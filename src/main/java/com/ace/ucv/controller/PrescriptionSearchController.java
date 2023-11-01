package com.ace.ucv.controller;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class PrescriptionSearchController {

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
            e.printStackTrace();
        }

        return patients;
    }
}