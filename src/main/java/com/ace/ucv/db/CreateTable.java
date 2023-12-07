package com.ace.ucv.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {

    // SQL statement for creating the 'diseases' table if it does not exist
    public static final String CREATE_DISEASE_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS diseases (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");

    // Logger for logging errors
    private static final Logger logger = LogManager.getLogger(CreateTable.class);

    // SQL statement for creating the 'patients' table if it does not exist
    public static final String CREATE_PATIENT_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,age INTEGER NOT NULL,field_of_work TEXT)");

    // SQL statement for creating the 'medications' table if it does not exist
    public static final String CREATE_MEDICATION_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS medications (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");

    // SQL statement for creating the 'prescriptions' table if it does not exist, including foreign key constraints
    public static final String CREATE_PRESCRIPTION_TABLE_SQL = String.format("CREATE TABLE IF NOT EXISTS prescriptions (id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,patient_id INTEGER NOT NULL,disease_id INTEGER NOT NULL,medication_id INTEGER NOT NULL,FOREIGN KEY(patient_id) REFERENCES patients(id),FOREIGN KEY(disease_id) REFERENCES diseases(id),FOREIGN KEY(medication_id) REFERENCES medications(id))");

    /**
     * Creates the necessary tables (patients, diseases, medications, and prescriptions) in the database.
     * This method executes SQL statements to create each table if it does not already exist.
     *
     * @param connection The connection to the database.
     */
    public static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Create the 'patients' table
            statement.execute(CREATE_PATIENT_TABLE_SQL);

            // Create the 'diseases' table
            statement.execute(CREATE_DISEASE_TABLE_SQL);

            // Create the 'medications' table
            statement.execute(CREATE_MEDICATION_TABLE_SQL);

            // Create the 'prescriptions' table
            statement.execute(CREATE_PRESCRIPTION_TABLE_SQL);

        } catch (SQLException e) {
            logger.error(String.format("Error creating tables: %s", e.getMessage()));
            throw new RuntimeException(String.format("Error creating tables: %s", e.getMessage()));
        }
    }
}
