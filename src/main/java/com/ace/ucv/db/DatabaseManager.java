package com.ace.ucv.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // Logger for logging errors
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    // URL for the SQLite database file
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/database.db";

    /**
     * Establishes a connection to the database using the JDBC driver.
     * The method attempts to create a connection to the specified SQLite database.
     * If the connection is unsuccessful, it logs the error and throws a runtime exception.
     *
     * @return A Connection object representing the connection to the database.
     */
    public static Connection connect() {
        try {
            // Attempt to establish a connection to the database
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            // Log the error if the connection attempt fails
            logger.error(String.format("Error connecting to the database%s", e.getMessage()));
            // Throw a runtime exception to indicate failure
            throw new RuntimeException(String.format("Database connection failed: %s", e.getMessage()));
        }
    }

    /**
     * Establishes a connection to the database.
     * Ensures that the SQLite JDBC driver is registered and attempts to establish a connection.
     * Logs an error and returns null if the driver is not found or the connection fails.
     *
     * @return A Connection object representing the connection to the database, or null if the connection fails.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Ensure the driver is registered
            Class.forName("org.sqlite.JDBC");
            // Establish a connection
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.error("Connection to database failed", e);
        }
        return connection;
    }
}
