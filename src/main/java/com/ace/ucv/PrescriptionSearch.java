package com.ace.ucv;

import com.ace.ucv.controller.PrescriptionSearchController;
import com.ace.ucv.model.Patient;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrescriptionSearch {
    private static final Logger logger = LogManager.getLogger(PrescriptionSearch.class);

    private TableView<Patient> table;
    private TextField minPrescriptionsField;
    private Button searchButton;
    private Label countLabel;
    private PrescriptionSearchController controller;
    private Stage stage;

    public PrescriptionSearch(Stage stage) {
        this.stage = stage;
        this.controller = new PrescriptionSearchController();
        this.table = new TableView<>();
        this.minPrescriptionsField = new TextField();
        this.searchButton = new Button("Search by prescriptions per month");
        this.countLabel = new Label();
        setupUI();
        setupActions();
    }

    private void setupUI() {
        minPrescriptionsField.setPromptText("Enter minimum prescriptions per month");

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(nameCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            try {
                int minPrescriptions = Integer.parseInt(minPrescriptionsField.getText());
                ObservableList<Patient> patients = controller.getPatientsWithPrescriptionCount(minPrescriptions);
                table.setItems(patients);
                countLabel.setText("Number of patients found: " + patients.size());
            } catch (NumberFormatException ex) {
                logger.error(String.format("Invalid input: %s", ex.getMessage()));
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Please enter a valid number");
                alert.showAndWait();
            }

            int minPrescriptions = Integer.parseInt(minPrescriptionsField.getText());
            ObservableList<Patient> patients = controller.getPatientsWithPrescriptionCount(minPrescriptions);
            table.setItems(patients);
            countLabel.setText("Number of patients found: " + patients.size());
        });
    }

    public void start() {
        VBox layout = new VBox(5);
        layout.getChildren().addAll(minPrescriptionsField, searchButton, countLabel, table);

        Scene scene = new Scene(layout, 600, 600);
        // Add stylesheets if necessary
        stage.setTitle("Search by Prescriptions per Month");
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}