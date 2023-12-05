package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.model.Medication;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class MedicationSearchByCategory {
    private TextField searchField;
    private Button searchButton;
    private TableView<Medication> table;
    private IMedicationSearchByCategory controller;
    private Alert errorAlert;

    public MedicationSearchByCategory(IMedicationSearchByCategory controller) {
        this.controller = controller;
        this.searchField = new TextField();
        this.searchButton = new Button("Search");
        this.table = new TableView<>();
        this.errorAlert = new Alert(Alert.AlertType.ERROR);
        setupUI();
        setupActions();
    }

    private void setupUI() {
        searchField.setPromptText("Enter medication category");

        TableColumn<Medication, String> nameCol = new TableColumn<>("Medication Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Medication, String> categoryCol = new TableColumn<>("Medication Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Medication, Number> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        table.getColumns().addAll(nameCol, categoryCol, countCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            try {
                String category = searchField.getText();
                ObservableList<Medication> medications = controller.getMedicationsByCategoryWithCount(category);
                table.setItems(medications);
            } catch (Exception ex) {
                displayError("Error", String.format("An error occurred: %s", ex.getMessage()));
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
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(searchField, searchButton, table);
        return vbox;
    }
}
