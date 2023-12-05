package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IDiseaseSearch;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class DiseaseSearch {
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private IDiseaseSearch controller;
    private Alert errorAlert;

    public DiseaseSearch(IDiseaseSearch controller) {
        this.controller = controller;
        this.table = new TableView<>();
        this.searchField = new TextField();
        this.searchButton = new Button("Search by disease");
        this.countLabel = new Label();
        this.errorAlert = new Alert(Alert.AlertType.ERROR);
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
                diseaseName = searchField.getText();
            }
            return new SimpleStringProperty(diseaseName);
        });

        table.getColumns().addAll(nameCol, diseaseCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            try {
                Pair<ObservableList<Patient>, Integer> result = controller.performSearch(searchField.getText());
                table.setItems(result.getKey());
                countLabel.setText("Number of patients found: " + result.getValue());
            } catch (Exception ex) {
                displayError("Database Error", String.format("An error occurred: %s", ex.getMessage()));
            }
        });
    }

    public Node getContent() {
        return createContent();
    }
    private VBox createContent() {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        return vbox;
    }

    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}
