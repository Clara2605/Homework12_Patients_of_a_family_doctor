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

    /**
     * Adds a new disease to the database.
     *
     * @param name The name of the disease to be added.
     */
    public void addDisease(String name) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DISEASE_SQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error adding disease to database", e);
        }
    }

    /**
     * Loads all diseases from the database.
     *
     * @return A list of Disease objects.
     */
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

    /**
     * Updates a disease's information in the database.
     *
     * @param disease The Disease object to be updated.
     * @param editedName The new name of the disease.
     */
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

    /**
     * Deletes a disease from the database.
     *
     * @param disease The Disease object to be deleted.
     */
    public void deleteDisease(Disease disease) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DISEASE_SQL)) {
            preparedStatement.setInt(1, disease.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Error deleting disease from database", e);
        }
    }

    /**
     * Handles SQL exceptions related to database operations.
     *
     * @param errorMessage The error message to log.
     * @param e The SQLException that was thrown.
     */
    private void handleSQLException(String errorMessage, SQLException e) {
        logger.error(String.format("%s: %s", errorMessage, e.getMessage()));
        throw new RuntimeException(errorMessage + ": " + e.getMessage());
    }
}
