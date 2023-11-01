//package com.ace.ucv.services;
//
//import com.ace.ucv.db.DatabaseManager;
//import com.ace.ucv.model.Medication;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MedicationService {
//    public void insertIntoDatabase(Medication medication) {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO medications (name, category) VALUES (?, ?)",
//                     Statement.RETURN_GENERATED_KEYS)) {
//            preparedStatement.setString(1, medication.getName());
//            preparedStatement.setString(2, medication.getCategory());
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
//}
//
//}
