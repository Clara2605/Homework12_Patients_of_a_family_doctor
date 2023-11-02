package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicationSearch {
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private Stage stage;
    private Alert errorAlert;

    public MedicationSearch(Stage stage) {
        this.stage = stage;
        this.table = new TableView<>();
        this.searchField = new TextField();
        this.searchButton = new Button("Search by medication");
        this.countLabel = new Label();
        this.errorAlert = new Alert(Alert.AlertType.ERROR);
        setupUI();
        setupActions();
    }

    private void setupUI() {
        searchField.setPromptText("Enter the name of the medication");

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> medicationCol = new TableColumn<>("Medication");
        medicationCol.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue();
            return new SimpleStringProperty(patient.getMedicationName());
        });

        table.getColumns().addAll(nameCol, medicationCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            try {
                ObservableList<Patient> patients = getPatientsWithMedication(searchField.getText());
                table.setItems(patients);
                countLabel.setText("Number of patients found: " + patients.size());
            } catch (Exception ex) {
                displayError("Database Error", "An error occurred: " + ex.getMessage());
            }
        });
    }

    private ObservableList<Patient> getPatientsWithMedication(String medicationName) throws SQLException {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String sql = "SELECT p.*, m.name as medication_name FROM patients p " +
                "JOIN prescriptions pr ON p.id = pr.patient_id " +
                "JOIN medications m ON m.id = pr.medication_id " +
                "WHERE m.name = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicationName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("field_of_work"),
                            rs.getString("medication_name"),
                            false
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return patients;
    }

    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public void start() {
        VBox layout = createContent();
        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Search Medications");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createContent() {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        return vbox;
    }
}
