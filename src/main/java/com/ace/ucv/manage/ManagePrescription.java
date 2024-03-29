package com.ace.ucv.manage;

import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.PrescriptionService;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class ManagePrescription {
    private static final Logger logger = LogManager.getLogger(ManagePrescription.class);
    private ObservableList<Patient> patients;
    private List<String> diseases;
    private List<String> medications;
    private TableView<Prescription> prescriptionTable;
    private IPrescriptionService prescriptionService;
    private IPatientService patientService;
    private ObservableList<Prescription> prescriptions;
    private Button editButton;
    private Button deleteButton;
    private Button addPrescriptionButton;

    /**
     * Constructor for ManagePrescription.
     * Initializes the prescription management interface with necessary services, data, and UI components.
     *
     * @param patients ObservableList of Patients to be used in the prescriptions.
     */
    public ManagePrescription(ObservableList<Patient> patients) {
        this.prescriptionService = new PrescriptionService();
        this.patientService = new PatientService();
        this.patients = patients;
        this.diseases = prescriptionService.loadItemsFromDatabase("diseases", "name");
        this.medications = prescriptionService.loadItemsFromDatabase("medications", "name");
        this.prescriptionTable = new TableView<>();

        addPrescriptionButton = new Button("Add Prescription");
        addPrescriptionButton.setOnAction(e -> openAddPrescriptionDialog());

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        setupButtons();
        setupPrescriptionTable();
        prescriptions = FXCollections.observableArrayList();
        prescriptions.addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }

    /**
     * Sets up the action buttons (Edit, Delete) for each prescription and defines their behavior.
     */
    private void setupButtons() {
        editButton.setOnAction(e -> editSelectedPrescription());
        deleteButton.setOnAction(e -> deleteSelectedPrescription());

        editButton.setDisable(true);
        deleteButton.setDisable(true);

        prescriptionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isItemSelected = newSelection != null;
            editButton.setDisable(!isItemSelected);
            deleteButton.setDisable(!isItemSelected);
        });
    }

    /**
     * Handles editing of a selected prescription by opening the edit dialog and refreshing the data.
     */
    private void editSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(
                    selectedPrescription,
                    prescriptionService,
                    prescriptionTable,
                    patients,
                    diseases,
                    medications
            );
            editPrescriptionDialog.show();
            loadPrescriptionsFromDatabase();
        }
    }

    /**
     * Handles deletion of a selected prescription after user confirmation.
     */
    private void deleteSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this prescription?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        if (prescriptionService.deletePrescription(selectedPrescription.getId())) {
                            prescriptions.remove(selectedPrescription); // Remove from the list
                            prescriptionTable.getItems().remove(selectedPrescription); // Remove from the table view
                        }
                    } catch (Exception ex) {
                        logger.error("Error deleting prescription: " + ex.getMessage());
                    }
                }
            });
        }
    }

    /**
     * Constructs the main content node for the prescription management interface.
     *
     * @return Node The main content node of the prescription management UI.
     */
    public Node getContent() {
        loadPatientsFromDatabase();
        loadPrescriptionsFromDatabase();

        HBox buttonBox = new HBox(addPrescriptionButton);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        VBox container = new VBox(buttonBox, prescriptionTable);
        container.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        container.setPadding(new Insets(10, 25, 10, 25));
        return container;
    }

    /**
     * Sets up the prescription table with necessary columns and data bindings.
     */
    private void setupPrescriptionTable() {
        TableColumn<Prescription, String> idColumn = createColumn("ID", "id");
        TableColumn<Prescription, String> dateColumn = createColumn("Date", "date");
        TableColumn<Prescription, String> patientNameColumn = createColumn("Patient", "patientName");
        TableColumn<Prescription, String> diseaseColumn = createColumn("Disease", "disease");
        TableColumn<Prescription, String> medicationColumn = createColumn("Medication", "medication");
        TableColumn<Prescription, Void> actionsColumn = createActionsColumn();

        prescriptionTable.getColumns().addAll(idColumn, dateColumn, patientNameColumn, diseaseColumn, medicationColumn, actionsColumn);
        setupColumnWidths(prescriptionTable, idColumn, dateColumn, patientNameColumn, diseaseColumn, medicationColumn, actionsColumn);
        prescriptionTable.refresh();

    }

    /**
     * Dynamically adjusts column widths in the prescription table.
     *
     * @param tableView The table view whose columns need to be adjusted.
     * @param columns Columns of the table view.
     */
    private void setupColumnWidths(TableView<Prescription> tableView, TableColumn<Prescription, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Prescription, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }

    /**
     * Creates a table column with the specified title and property.
     *
     * @param title Title of the column.
     * @param property Property name used for binding data to the column.
     * @param <T> Type of the data displayed in the column.
     * @return TableColumn A new table column.
     */
    private <T> TableColumn<Prescription, T> createColumn(String title, String property) {
        TableColumn<Prescription, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    /**
     * Creates an actions column with buttons for editing and deleting prescriptions.
     *
     * @return TableColumn A new actions column.
     */
    private TableColumn<Prescription, Void> createActionsColumn() {
        TableColumn<Prescription, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new ActionCell());
        return actionsColumn;
    }

    // The ActionCell class implementation
    private class ActionCell extends TableCell<Prescription, Void> {
        private final Button editButton = new Button("Edit");
        private final Button deleteButton = new Button("Delete");

        public ActionCell() {
            setupEditButton(editButton);
            setupDeleteButton(deleteButton);
            editButton.getStyleClass().add("edit-button");
            deleteButton.getStyleClass().add("delete-button");
            HBox buttons = new HBox(editButton, deleteButton);
            buttons.setSpacing(10); // Adjust spacing as needed
            setGraphic(buttons);
        }

        private void setupEditButton(Button button) {
            button.setOnAction(e -> handleEdit());
        }

        private void setupDeleteButton(Button button) {
            button.setOnAction(e -> handleDelete());
        }

        private void handleEdit() {
            Prescription selectedPrescription = getTableView().getItems().get(getIndex());
            EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(
                    selectedPrescription,
                    prescriptionService,
                    prescriptionTable,
                    patients,
                    diseases,
                    medications
            );
            editPrescriptionDialog.show();
            loadPrescriptionsFromDatabase();
        }

        private void handleDelete() {
            Prescription selectedPrescription = getTableView().getItems().get(getIndex());
            try {
                if (prescriptionService.deletePrescription(selectedPrescription.getId())) {
                    getTableView().getItems().remove(getIndex()); // Remove directly from the table view
                    prescriptions.remove(selectedPrescription); // Also remove from the list if necessary
                }
            } catch (Exception ex) {
                logger.error("Error in ActionCell handleDelete: " + ex.getMessage());
            }
        }


        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(editButton, deleteButton);
                buttons.setSpacing(10);
                buttons.setAlignment(javafx.geometry.Pos.CENTER);
                setGraphic(buttons);
            }
        }
    }

    /**
     * Loads patient data from the database and updates the patients ObservableList.
     */
    private void loadPatientsFromDatabase() {
        try {
            List<Patient> patientsList = patientService.loadPatientsFromDatabase(); // Use PatientService to load patients
            patients.clear();
            patients.addAll(patientsList);
        } catch (Exception e) {
            logger.error(String.format("Error loading patients from the database: %s", e.getMessage()));
        }
    }

    /**
     * Loads prescription data from the database and updates the prescriptionTable.
     */
    private void loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();
        prescriptionTable.setItems(prescriptions);
        prescriptionTable.refresh();
    }

    /**
     * Opens the dialog for adding a new prescription.
     */
    private void openAddPrescriptionDialog() {
        AddPrescriptionDialog addPrescriptionDialog = new AddPrescriptionDialog(patients, diseases, medications, prescriptionService, prescriptionTable);
        addPrescriptionDialog.show();
        loadPrescriptionsFromDatabase();
    }
}