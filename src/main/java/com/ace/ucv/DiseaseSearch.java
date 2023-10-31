package com.ace.ucv;

import com.ace.ucv.controller.DiseaseSearchController;
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
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiseaseSearch {
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private DiseaseSearchController controller;
    private Stage stage;

    public DiseaseSearch(Stage stage) {
        this.stage = stage;
        this.controller = new DiseaseSearchController();
        this.table = new TableView<>();
        this.searchField = new TextField();
        this.searchButton = new Button("Search by disease");
        this.countLabel = new Label();
        setupUI();
        setupActions();
    }

    private void setupUI() {
        searchField.setPromptText("Enter the name of the disease");

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> diseaseCol = new TableColumn<>("Disease");
        diseaseCol.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue();
            String diseaseName = patient.getDiseaseName();
            if (diseaseName == null || diseaseName.isEmpty()) {
                diseaseName = searchField.getText(); // Use text from searchField if no disease is assigned
            }
            return new SimpleStringProperty(diseaseName);
        });

        table.getColumns().addAll(nameCol, diseaseCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            Pair<ObservableList<Patient>, Integer> result = controller.performSearch(searchField.getText());
            table.setItems(result.getKey());
            countLabel.setText("Number of patients found: " + result.getValue());
        });
    }

    private void updatePatientsWithDisease(String diseaseName) {
        ObservableList<Patient> allPatients = getAllPatients(); // Fetch all patients
        boolean diseaseFound = false;

        for (Patient patient : allPatients) {
            if (isPatientHavingDisease(patient.getId(), diseaseName)) {
                patient.setDiseaseName(diseaseName); // Set disease name if found
                diseaseFound = true;
            } else {
                patient.setDiseaseName("No Disease Assigned");
            }
        }

        if (diseaseFound) {
            table.setItems(allPatients);
            countLabel.setText("Disease found and updated for patients.");
        } else {
            countLabel.setText("No patients found for this disease.");
            table.setItems(FXCollections.observableArrayList()); // Clear table
        }
    }


    public void start() {
        VBox layout = createContent();
        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Search Diseases");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createContent() {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        return vbox;
    }

    private ObservableList<Patient> getAllPatients() {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String sql = "SELECT * FROM patients"; // Replace with your actual SQL query

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("field_of_work")
                        // Include other fields as necessary
                );
                patient.setDiseaseName(null); // Initially set disease name as empty or null
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    private boolean isPatientHavingDisease(int patientId, String diseaseName) {
        String sql = "SELECT COUNT(*) FROM prescriptions WHERE patient_id = ? AND disease_id = (SELECT id FROM diseases WHERE name = ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setString(2, diseaseName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
