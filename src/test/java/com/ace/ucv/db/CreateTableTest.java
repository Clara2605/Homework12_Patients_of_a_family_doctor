package com.ace.ucv.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class CreateTableTest {

        @Test
        public void test_create_table() {
            // Arrange
            Connection connection = DatabaseManager.connect();

            // Act
            CreateTable.createTable(connection);

            // Assert
            assertNotNull(connection);
        }

    @Test
    public void test_raise_exception_with_invalid_connection_object() {
        // Arrange
        Connection connection = null;

        // Act and Assert
        assertThrows(RuntimeException.class, () -> CreateTable.createTable(connection));
    }
}