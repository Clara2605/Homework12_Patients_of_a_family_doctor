package com.ace.ucv.controller;

import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class MedicationSearchByCategoryController implements IMedicationSearchByCategory {

    private static final Logger logger = LogManager.getLogger(MedicationSearchByCategoryController.class);
    private static final String GET_MEDICATIONS_BY_CATEGORY_SQL = "SELECT m.id, m.name, m.category, COUNT(p.medication_id) AS medication_count " +
            "FROM medications m " +
            "LEFT JOIN prescriptions p ON m.id = p.medication_id " +
            "WHERE m.category = ? " +
            "GROUP BY m.id";

    public ObservableList<Medication> getMedicationsByCategoryWithCount(String category) {
        ObservableList<Medication> medications = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(GET_MEDICATIONS_BY_CATEGORY_SQL)) {
            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medication medication = new Medication(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category")
                    );
                    int count = rs.getInt("medication_count");
                    medication.setCount(count);
                    medications.add(medication);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting medications by category: %s due to: %s", category, e.getMessage()));
            throw new RuntimeException("Error getting medications by category: " + e.getMessage());
        }
        return medications;
    }
}
