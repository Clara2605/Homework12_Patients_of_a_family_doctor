package com.ace.ucv.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {

    private static final String PATIENTS_TABLE = "patients";
    private static final String DISEASES_TABLE = "diseases";
    private static final String MEDICATIONS_TABLE = "medications";
    private static final String PRESCRIPTIONS_TABLE = "prescriptions";

    public static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            createPatientsTable(statement);
            createDiseasesTable(statement);
            createMedicationsTable(statement);
            createPrescriptionsTable(statement);
            addOneToOneRelationships(statement);
        } catch (SQLException e) {
            throw new SQLException("Error creating tables: " + e.getMessage(), e);
        }
    }

    private static void createPatientsTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + PATIENTS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "age INTEGER, " +
                "field_of_work TEXT)";
        statement.execute(sql);
    }

    private static void createDiseasesTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + DISEASES_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT)";
        statement.execute(sql);
    }

    private static void createMedicationsTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MEDICATIONS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category TEXT)";
        statement.execute(sql);
    }

    private static void createPrescriptionsTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + PRESCRIPTIONS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "patient_id INTEGER, " +
                "disease_id INTEGER, " +
                "medication_id INTEGER, " +
                "FOREIGN KEY (patient_id) REFERENCES " + PATIENTS_TABLE + "(id), " +
                "FOREIGN KEY (disease_id) REFERENCES " + DISEASES_TABLE + "(id), " +
                "FOREIGN KEY (medication_id) REFERENCES " + MEDICATIONS_TABLE + "(id))";
        statement.execute(sql);
    }

    private static void addOneToOneRelationships(Statement statement) throws SQLException {
        String diseaseSql = "ALTER TABLE " + DISEASES_TABLE + " " +
                "ADD COLUMN prescription_id INTEGER, " +
                "FOREIGN KEY (prescription_id) REFERENCES " + PRESCRIPTIONS_TABLE + "(id)";
        statement.execute(diseaseSql);

        String medicationSql = "ALTER TABLE " + MEDICATIONS_TABLE + " " +
                "ADD COLUMN prescription_id INTEGER, " +
                "FOREIGN KEY (prescription_id) REFERENCES " + PRESCRIPTIONS_TABLE + "(id)";
        statement.execute(medicationSql);
    }
}
