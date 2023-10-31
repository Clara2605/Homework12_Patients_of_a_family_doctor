package com.ace.ucv.controller;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiseaseSearchController {

    private ObservableList<Patient> data = FXCollections.observableArrayList();

    public Pair<ObservableList<Patient>, Integer> performSearch(String diseaseName) {
        List<Patient> patients = getPatientsWithDisease(diseaseName);
        data.setAll(patients);
        return new Pair<>(data, patients.size());
    }

    private List<Patient> getPatientsWithDisease(String diseaseName) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.* FROM patients p " +
                "JOIN prescriptions pr ON p.id = pr.patient_id " +
                "JOIN diseases d ON d.id = pr.disease_id " +
                "WHERE d.name = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, diseaseName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("field_of_work")

                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}
