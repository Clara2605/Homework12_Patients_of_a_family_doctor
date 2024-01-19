package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IDiseaseSearch;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

    /**
     * Constructor for DiseaseSearch class.
     * Initializes the components and sets up UI and actions.
     *
     * @param controller Controller that handles the disease search logic.
     */
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

    /**
     * Sets up the user interface elements for the disease search functionality.
     * This includes setting up table columns and other UI components.
     */
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
        setupColumnWidths(table, nameCol, diseaseCol);

    }
    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }

    /**
     * Configures the actions for the search button and other interactive elements.
     * This method defines the behavior upon button click and error handling.
     */
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

    /**
     * Creates and returns the main content Node of the DiseaseSearch UI.
     *
     * @return Node representing the assembled UI layout.
     */
    public Node getContent() {
        return createContent();
    }

    /**
     * Constructs the VBox containing all UI elements for the DiseaseSearch feature.
     *
     * @return VBox with search field, button, label, and table.
     */
    private VBox createContent() {
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        vbox.setPadding(new Insets(10, 25, 10, 25));

        return vbox;
    }

    /**
     * Displays an error alert with a given title and message.
     *
     * @param title   Title of the error alert.
     * @param message Message to be displayed in the error alert.
     */
    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}
