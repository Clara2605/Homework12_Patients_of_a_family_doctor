package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IMedicationSearch;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class MedicationSearch {
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private Alert errorAlert;
    private IMedicationSearch controller;

    public MedicationSearch(IMedicationSearch controller) {
        this.controller = controller;
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
                String medicationName = searchField.getText();
                ObservableList<Patient> patients = controller.performSearch(medicationName).getKey();
                table.setItems(patients);
                countLabel.setText("Number of patients found: " + patients.size());
            } catch (Exception ex) {
                displayError("Database Error", String.format("An error occurred: %s", ex.getMessage()));
            }
        });
    }

    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public Node getContent() {
        VBox layout = createContent();
        return layout;
    }

    private VBox createContent() {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        return vbox;
    }
}
