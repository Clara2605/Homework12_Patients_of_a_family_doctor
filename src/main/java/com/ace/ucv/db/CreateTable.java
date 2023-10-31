package com.ace.ucv.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void createTable() {
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement()) {
            // Create the patients table
            String createPatientsTableSQL = "CREATE TABLE IF NOT EXISTS patients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "age INTEGER, " +
                    "field_of_work TEXT" +
                    ")";
            statement.execute(createPatientsTableSQL);

            // Create the diseases table
            String createDiseasesTableSQL = "CREATE TABLE IF NOT EXISTS diseases (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT" +
                    ")";
            statement.execute(createDiseasesTableSQL);

            // Create the medications table
            String createMedicationsTableSQL = "CREATE TABLE IF NOT EXISTS medications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "category TEXT" +
                    ")";
            statement.execute(createMedicationsTableSQL);

            // Create the prescriptions table
            String createPrescriptionsTableSQL = "CREATE TABLE IF NOT EXISTS prescriptions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date TEXT, " +
                    "patient_id INTEGER, " +
                    "disease_id INTEGER, " +
                    "medication_id INTEGER, " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(id), " +
                    "FOREIGN KEY (disease_id) REFERENCES diseases(id), " +
                    "FOREIGN KEY (medication_id) REFERENCES medications(id)" +
                    ")";
            statement.execute(createPrescriptionsTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
