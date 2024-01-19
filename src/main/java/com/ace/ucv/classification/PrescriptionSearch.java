package com.ace.ucv.classification;

import com.ace.ucv.controller.interfaces.IPrescriptionSearch;
import com.ace.ucv.model.Patient;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrescriptionSearch {
    private static final Logger logger = LogManager.getLogger(PrescriptionSearch.class);

    private TableView<Patient> table;
    private TextField minPrescriptionsField;
    private Button searchButton;
    private Label countLabel;
    private IPrescriptionSearch controller;

    /**
     * Constructor for PrescriptionSearch class.
     * Initializes the components and sets up the user interface and actions.
     *
     * @param controller Controller that handles the prescription search logic.
     */
    public PrescriptionSearch(IPrescriptionSearch controller) {
        this.controller = controller;
        this.table = new TableView<>();
        this.minPrescriptionsField = new TextField();
        this.searchButton = new Button("Search by prescriptions per month");
        this.countLabel = new Label();
        setupUI();
        setupActions();
    }

    /**
     * Sets up the user interface elements for the prescription search functionality.
     * This includes configuring the table columns and other UI components.
     */
    private void setupUI() {
        minPrescriptionsField.setPromptText("Enter minimum prescriptions per month");

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(nameCol);
        setupColumnWidths(table, nameCol);
    }

    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
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

    /**
     * Creates and returns the main content Node of the PrescriptionSearch UI.
     *
     * @return Node representing the assembled UI layout.
     */
    public Node getContent() {
        VBox layout = createContent();
        return layout;
    }

    /**
     * Constructs the VBox containing all UI elements for the PrescriptionSearch feature.
     *
     * @return VBox with the minimum prescriptions field, search button, count label, and table.
     */
    private VBox createContent() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 25, 10, 25));
        vbox.getChildren().addAll(minPrescriptionsField, searchButton, countLabel, table);
        return vbox;
    }
}