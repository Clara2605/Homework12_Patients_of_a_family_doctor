package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IMedicationSearchByCategory;
import com.ace.ucv.model.Medication;
import javafx.geometry.Insets;
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

    /**
     * Constructor for MedicationSearchByCategory class.
     * Initializes the components and sets up the user interface and actions.
     *
     * @param controller Controller that handles the medication search by category logic.
     */
    public MedicationSearchByCategory(IMedicationSearchByCategory controller) {
        this.controller = controller;
        this.searchField = new TextField();
        this.searchButton = new Button("Search");
        this.table = new TableView<>();
        this.errorAlert = new Alert(Alert.AlertType.ERROR);
        setupUI();
        setupActions();
    }

    /**
     * Sets up the user interface elements for the medication search by category functionality.
     * This includes preparing the table columns and other UI components.
     */
    private void setupUI() {
        searchField.setPromptText("Enter medication category");

        TableColumn<Medication, String> nameCol = new TableColumn<>("Medication Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Medication, String> categoryCol = new TableColumn<>("Medication Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Medication, Number> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        table.getColumns().addAll(nameCol, categoryCol, countCol);
        setupColumnWidths(table, nameCol, categoryCol, countCol);
    }

    private void setupColumnWidths(TableView<Medication> tableView, TableColumn<Medication, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Medication, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }


    /**
     * Configures the actions for the search button and other interactive elements.
     * Defines the behavior for button clicks, including searching for medications based on category and error handling.
     */
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

    /**
     * Displays an error alert with the specified title and message.
     *
     * @param title   Title of the error alert.
     * @param message Message to be displayed in the alert.
     */
    private void displayError(String title, String message) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    /**
     * Creates and returns the main content Node of the MedicationSearchByCategory UI.
     *
     * @return Node representing the assembled UI layout.
     */
    public Node getContent() {
        VBox layout = createContent();
        return layout;
    }

    /**
     * Constructs the VBox containing all UI elements for the MedicationSearchByCategory feature.
     *
     * @return VBox with search field, button, and table.
     */
    private VBox createContent() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 25, 10, 25));
        vbox.getChildren().addAll(searchField, searchButton, table);
        return vbox;
    }
}
