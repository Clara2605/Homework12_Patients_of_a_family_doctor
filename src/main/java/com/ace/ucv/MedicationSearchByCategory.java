package com.ace.ucv;

import com.ace.ucv.controller.MedicationSearchByCategoryController;
import com.ace.ucv.model.Medication;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class MedicationSearchByCategory {
    private Stage stage;
    private TextField searchField;
    private Button searchButton;
    private TableView<Medication> table;
    private MedicationSearchByCategoryController controller;
    private Alert errorAlert;

    public MedicationSearchByCategory(Stage stage) {
        this.stage = stage;
        this.controller = new MedicationSearchByCategoryController();
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
                displayError("Error", "An error occurred: " + ex.getMessage());
            }
        });
    }

    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public void start() {
        VBox layout = new VBox(10);
        layout.getChildren().addAll(searchField, searchButton, table);
        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Medication Search By Category");
        stage.setScene(scene);
        stage.show();
    }
}
