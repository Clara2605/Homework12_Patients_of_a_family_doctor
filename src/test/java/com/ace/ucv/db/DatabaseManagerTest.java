package com.ace.ucv.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    @Test
    public void test_connect_to_database_successfully() {
        // Arrange
        Connection connection = DatabaseManager.connect();

        // Act

        // Assert
        assertNotNull(connection);
    }

    @Test
    public void test_handles_multiple_connections() {
        // Arrange
        Connection connection1 = DatabaseManager.connect();
        Connection connection2 = DatabaseManager.connect();

        // Act

        // Assert
        assertNotNull(connection1);
        assertNotNull(connection2);
        assertNotEquals(connection1, connection2);
    }
    @Test
    public void testConnectionToDatabaseIsOpen() {
        try (Connection connection = DatabaseManager.connect()) {
            assertNotNull(connection, "Conexiunea nu ar trebui să fie null.");
            assertFalse(connection.isClosed(), "Conexiunea ar trebui să fie deschisă.");
        } catch (SQLException e) {
            fail("Ar trebui să se conecteze la baza de date fără a arunca o excepție.");
        }
    }

    @Test
    public void testConnectionClosesCorrectly() {
        Connection connection = DatabaseManager.connect();
        try {
            assertNotNull(connection, "Conexiunea nu ar trebui să fie null.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    assertTrue(connection.isClosed(), "Conexiunea ar trebui să fie închisă după utilizare.");
                } catch (SQLException e) {
                    fail("Închiderea conexiunii nu ar trebui să arunce o excepție.");
                }
            }
        }
    }

}