package com.ace.ucv.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/database.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            logger.error(String.format("Error connecting to the database%s", e.getMessage()));
            throw new RuntimeException(String.format("Database connection failed: %s", e.getMessage()));
        }
    }
}
