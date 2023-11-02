package com.ace.ucv.controller;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MedicationSearchByCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(MedicationSearchByCategoryController.class);

    public ObservableList<Medication> getMedicationsByCategoryWithCount(String category) {
        ObservableList<Medication> medications = FXCollections.observableArrayList();

        String sql = "SELECT m.id, m.name, m.category, COUNT(p.medication_id) AS medication_count " +
                "FROM medications m " +
                "LEFT JOIN prescriptions p ON m.id = p.medication_id " +
                "WHERE m.category = ? " +
                "GROUP BY m.id";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medication medication = new Medication(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category")
                    );
                    int count = rs.getInt("medication_count");
                    medication.setCount(count); // Presupunând că există un câmp 'count' în clasa Medication
                    medications.add(medication);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting medications by category: " + category, e);
            throw new RuntimeException("Error getting medications by category: " + e.getMessage(), e);
        }
        return medications;
    }
}
