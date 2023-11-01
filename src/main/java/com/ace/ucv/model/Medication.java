package com.ace.ucv.model;

import com.ace.ucv.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Medication {
    private int id;
    private String name;
    private String category;
    private int count;

    public Medication(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
    public Medication(int id, String name, String category, int count) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.count = count;
    }
    public Medication(String name, String category) {
        this.name = name;
        this.category = category;
    }
    // Getter and setter for count
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int anInt) {
        this.id = anInt;
    }


//    public void insertIntoDatabase() {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO medications (name, category) VALUES (?, ?)",
//                     Statement.RETURN_GENERATED_KEYS)) {
//            preparedStatement.setString(1, name);
//            preparedStatement.setString(2, category);
//            preparedStatement.executeUpdate();
//
//            // După inserare, obțineți ID-ul medicamentului inserat
//            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    int generatedId = generatedKeys.getInt(1); // Aici obțineți ID-ul generat
//                    this.id = generatedId;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void editMedication(String editedName, String editedCategory) {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE medications SET name=?, category=? WHERE id=?")) {
//            preparedStatement.setString(1, editedName);
//            preparedStatement.setString(2, editedCategory);
//            preparedStatement.setInt(3, id);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        this.name = editedName;
//        this.category = editedCategory;
//    }
//
//    public void deleteMedication() {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM medications WHERE id=?")) {
//            preparedStatement.setInt(1, id);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static List<Medication> loadMedicationsFromDatabase() {
//        List<Medication> medications = new ArrayList<>();
//        try (Connection connection = DatabaseManager.connect();
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery("SELECT * FROM medications")) {
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                String category = resultSet.getString("category");
//                Medication medication = new Medication(id, name, category);
//                medications.add(medication);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return medications;
//    }
}
