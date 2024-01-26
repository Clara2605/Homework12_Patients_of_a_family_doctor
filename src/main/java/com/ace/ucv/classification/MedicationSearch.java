package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IMedicationSearch;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MedicationSearch {
    public static final String ENTER_THE_NAME_OF_THE_MEDICATION = "Enter the name of the medication";
    public static final int SPACING = 10;
    public static final int RIGHT_LEFT_SPACING = 25;
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private Alert errorAlert;
    private IMedicationSearch controller;

    /**
     * Constructor for MedicationSearch class.
     * Initializes the components and sets up the user interface and actions.
     *
     * @param controller Controller that handles the medication search logic.
     */
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

    /**
     * Sets up the user interface elements for the medication search functionality.
     * This includes preparing the table columns and other UI components.
     */
    private void setupUI() {
        searchField.setPromptText(ENTER_THE_NAME_OF_THE_MEDICATION);

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> medicationCol = new TableColumn<>("Medication");
        medicationCol.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue();
            return new SimpleStringProperty(patient.getMedicationName());
        });

        table.getColumns().addAll(nameCol, medicationCol);
        setupColumnWidths(table, nameCol, medicationCol);
    }

    /**
     * Sets the table column widths proportionally.
     * @param tableView The table to set the column widths for.
     * @param columns The columns of the table.
     */
    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }

    /**
     * Configures the actions for the search button and other interactive elements.
     * Defines the behavior for button clicks, including searching and error handling.
     */
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
     * Creates and returns the main content Node of the MedicationSearch UI.
     *
     * @return Node representing the assembled UI layout.
     */
    public Node getContent() {
        VBox layout = createContent();
        return layout;
    }

    /**
     * Constructs the VBox containing all UI elements for the MedicationSearch feature.
     *
     * @return VBox with search field, button, label, and table.
     */
    private VBox createContent() {
        VBox vbox = new VBox(SPACING);
        vbox.setPadding(new Insets(SPACING, RIGHT_LEFT_SPACING, SPACING, RIGHT_LEFT_SPACING));
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        return vbox;
    }
}
