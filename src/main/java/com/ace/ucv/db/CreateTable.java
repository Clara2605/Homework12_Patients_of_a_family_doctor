package com.ace.ucv.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {

    public static final String CREATE_DISEASE_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS diseases (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");
    private static final Logger logger = LogManager.getLogger(CreateTable.class);
    public static final String CREATE_PATIENT_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,age INTEGER NOT NULL,field_of_work TEXT)");
    public static final String CREATE_MEDICATION_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS medications (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");
    public static final String CREATE_PRESCRIPTION_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS prescriptions (id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,patient_id INTEGER NOT NULL,disease_id INTEGER NOT NULL,medication_id INTEGER NOT NULL,FOREIGN KEY(patient_id) REFERENCES patients(id),FOREIGN KEY(disease_id) REFERENCES diseases(id),FOREIGN KEY(medication_id) REFERENCES medications(id))");

    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Tabelul patients
            statement.execute(CREATE_PATIENT_TABLE_SQL);

            // Tabelul diseases
            statement.execute(CREATE_DISEASE_TABLE_SQL);

            // Tabelul medications
            statement.execute(CREATE_MEDICATION_TABLE_SQL);

            // Tabelul prescriptions
            statement.execute(CREATE_PRESCRIPTION_TABLE_SQL);

        } catch (SQLException e) {
            logger.error(String.format("Error creating tables: %s", e.getMessage()));
            throw new RuntimeException(String.format("Error creating tables: %s", e.getMessage()));
        }
    }
}
