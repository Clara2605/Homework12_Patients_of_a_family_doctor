package com.ace.ucv.model;

import com.ace.ucv.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Disease {
    private int id;
    private String name;

    public Disease(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Disease(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public void insertIntoDatabase() {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO diseases (name) VALUES (?)")) {
//            preparedStatement.setString(1, name);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void addDisease(String name) {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO diseases (name) VALUES (?)")) {
//            preparedStatement.setString(1, name);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static List<Disease> loadDiseasesFromDatabase() {
//        List<Disease> diseases = new ArrayList<>();
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM diseases");
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                Disease disease = new Disease(id, name);
//                diseases.add(disease);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return diseases;
//    }
//
//    public void editDisease(String editedName) {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE diseases SET name=? WHERE id=?")) {
//            preparedStatement.setString(1, editedName);
//            preparedStatement.setInt(2, this.id);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        this.name = editedName;
//    }
//
//    public void deleteDisease() {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM diseases WHERE id=?")) {
//            preparedStatement.setInt(1, this.id);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
