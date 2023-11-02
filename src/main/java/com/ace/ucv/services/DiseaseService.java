package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.services.interfaces.IDiseaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiseaseService implements IDiseaseService {
    public void insertIntoDatabase(Disease disease) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO diseases (name) VALUES (?)")) {
            preparedStatement.setString(1, disease.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDisease(String name) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO diseases (name) VALUES (?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Disease> loadDiseasesFromDatabase() {
        List<Disease> diseases = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM diseases");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Disease disease = new Disease(id, name);
                diseases.add(disease);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diseases;
    }

    public void editDisease(Disease disease, String editedName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE diseases SET name=? WHERE id=?")) {
            preparedStatement.setString(1, editedName);
            preparedStatement.setInt(2, disease.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        disease.setName(editedName);
    }

    public void deleteDisease(Disease disease) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM diseases WHERE id=?")) {
            preparedStatement.setInt(1, disease.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
