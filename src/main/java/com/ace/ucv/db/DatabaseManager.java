package com.ace.ucv.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:D:/Anul1 IS1.1/MISC/MSIC_PROIECT/src/main/resources/database/database.db"; // Schimbă cu calea către baza ta de date

//    public static Connection connect() {
//        try {
//            return DriverManager.getConnection(DATABASE_URL);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            connection.setAutoCommit(false); // Disable auto-commit
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
