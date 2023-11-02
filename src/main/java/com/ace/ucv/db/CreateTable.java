package com.ace.ucv.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {

    private static final Logger logger = LoggerFactory.getLogger(CreateTable.class);

    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Tabelul patients
            statement.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "age INTEGER NOT NULL," +
                    "field_of_work TEXT)");

            // Tabelul diseases
            statement.execute("CREATE TABLE IF NOT EXISTS diseases (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL)");

            // Tabelul medications
            statement.execute("CREATE TABLE IF NOT EXISTS medications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL)");

            // Tabelul prescriptions
            statement.execute("CREATE TABLE IF NOT EXISTS prescriptions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date TEXT NOT NULL," +
                    "patient_id INTEGER NOT NULL," +
                    "disease_id INTEGER NOT NULL," +
                    "medication_id INTEGER NOT NULL," +
                    "FOREIGN KEY(patient_id) REFERENCES patients(id)," +
                    "FOREIGN KEY(disease_id) REFERENCES diseases(id)," +
                    "FOREIGN KEY(medication_id) REFERENCES medications(id))");

        } catch (SQLException e) {
            logger.error("Error creating tables", e);
            throw new RuntimeException("Error creating tables: " + e.getMessage(), e);
        }
    }
}
