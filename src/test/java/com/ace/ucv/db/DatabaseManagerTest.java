//package com.ace.ucv.db;
//
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DatabaseManagerTest {
//    @Test
//    public void test_connect_to_database_successfully() {
//        // Arrange
//        Connection connection = DatabaseManager.connect();
//
//        // Act
//
//        // Assert
//        assertNotNull(connection);
//    }
//
//    @Test
//    public void test_handles_multiple_connections() {
//        // Arrange
//        Connection connection1 = DatabaseManager.connect();
//        Connection connection2 = DatabaseManager.connect();
//
//        // Act
//
//        // Assert
//        assertNotNull(connection1);
//        assertNotNull(connection2);
//        assertNotEquals(connection1, connection2);
//    }
//
//}