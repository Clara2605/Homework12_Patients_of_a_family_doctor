package com.ace.ucv.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/database.db"; // Schimbă cu calea către baza ta de date

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
