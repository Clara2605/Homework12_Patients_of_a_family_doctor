package com.ace.ucv.repositories;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiseaseRepository {
    private static final Logger logger = LogManager.getLogger(DiseaseRepository.class);

    private static final String INSERT_DISEASE_SQL =
            "INSERT INTO diseases (name) VALUES (?)";
    private static final String UPDATE_DISEASE_SQL =
            "UPDATE diseases SET name=? WHERE id=?";
    private static final String DELETE_DISEASE_SQL =
            "DELETE FROM diseases WHERE id=?";
    private static final String SELECT_DISEASE_SQL =
            "SELECT * FROM diseases";

    public void addDisease(String name) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DISEASE_SQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error adding disease to database", e);
        }
    }

    public List<Disease> loadDiseasesFromDatabase() {
        List<Disease> diseases = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DISEASE_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                diseases.add(new Disease(id, name));
            }
        } catch (SQLException e) {
            handleSQLException("Error loading diseases from database", e);
        }
        return diseases;
    }

    public void editDisease(Disease disease, String editedName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DISEASE_SQL)) {
            preparedStatement.setString(1, editedName);
            preparedStatement.setInt(2, disease.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error editing disease in database", e);
        }

        disease.setName(editedName);
    }

    public void deleteDisease(Disease disease) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DISEASE_SQL)) {
            preparedStatement.setInt(1, disease.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error deleting disease from database", e);
        }
    }

    private void handleSQLException(String errorMessage, SQLException e) {
        logger.error(errorMessage + ": " + e.getMessage());
        throw new RuntimeException(errorMessage + ": " + e.getMessage());
    }
}
