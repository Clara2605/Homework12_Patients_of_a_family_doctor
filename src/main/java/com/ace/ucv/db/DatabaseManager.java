package com.ace.ucv.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/database.db"; // Schimbă cu calea către baza ta de date

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            logger.error("Error connecting to the database", e);
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }
}
