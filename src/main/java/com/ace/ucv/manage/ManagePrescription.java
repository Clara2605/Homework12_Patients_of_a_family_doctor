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
     * Initializes the class with a list of patients and sets up the prescription service.
     *
     * @param patients ObservableList of Patients.
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
     * Sets up action buttons for managing prescriptions and their listener events.
     */
    private void setupButtons() {
        editButton.setOnAction(e -> editSelectedPrescription());
        deleteButton.setOnAction(e -> deleteSelectedPrescription());

        // Disabling buttons initially
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable buttons only when a row is selected
        prescriptionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isItemSelected = newSelection != null;
            editButton.setDisable(!isItemSelected);
            deleteButton.setDisable(!isItemSelected);
        });
    }

    /**
     * Handles the action for editing a selected prescription.
     * Opens the EditPrescriptionDialog for the selected prescription.
     */
    private void editSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(selectedPrescription, prescriptionService, prescriptionTable);
            editPrescriptionDialog.show();
        }
    }

    /**
     * Handles the action for deleting a selected prescription.
     * Shows a confirmation dialog before deletion.
     */
    private void deleteSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this prescription?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (prescriptionService.deletePrescription(selectedPrescription.getId())) {
                        prescriptionTable.getItems().remove(selectedPrescription);
                    }
                }
            });
        }
    }

    /**
     * Creates the content of the manage prescriptions section.
     * Sets up the layout, form fields, and table for managing prescriptions.
     *
     * @return Node representing the layout of the manage prescriptions section.
     */
    public Node getContent() {
        loadPatientsFromDatabase();
        loadPrescriptionsFromDatabase();

        HBox buttonBox = new HBox(addPrescriptionButton);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        VBox container = new VBox(buttonBox, prescriptionTable);
        container.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        container.setPadding(new Insets(10, 10, 10, 10));
        return container;
    }

    /**
     * Sets up and configures the TableView for displaying prescriptions.
     */
    private void setupPrescriptionTable() {
        prescriptionTable.getColumns().addAll(
                createColumn("ID", "id"),
                createColumn("Date", "date"),
                createColumn("Disease", "disease"),
                createColumn("Medication", "medication"),
                createActionsColumn()
        );
    }

    /**
     * Creates a TableColumn for a specified property of Prescription.
     *
     * @param title Title of the column.
     * @param property Property name of the Prescription to bind to the column.
     * @return Configured TableColumn.
     */
    private <T> TableColumn<Prescription, T> createColumn(String title, String property) {
        TableColumn<Prescription, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    /**
     * Creates a TableColumn for actions (edit/delete) on each Prescription.
     *
     * @return Configured TableColumn for actions.
     */
    private TableColumn<Prescription, Void> createActionsColumn() {
        TableColumn<Prescription, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new ActionCell());
        return actionsColumn;
    }

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
            EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(selectedPrescription, prescriptionService, prescriptionTable);
            editPrescriptionDialog.show();
        }

        private void handleDelete() {
            Prescription selectedPrescription = getTableView().getItems().get(getIndex());
            prescriptionService.deletePrescription(selectedPrescription.getId());
            prescriptions.remove(selectedPrescription);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(editButton, deleteButton);
                setGraphic(buttons);
            }
        }
    }

    /**
     * Loads patients from the database and updates the patients list.
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
     * Loads prescriptions from the database and updates the prescription table.
     */
    private void loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();
        prescriptionTable.setItems(prescriptions);
    }

    /**
     * Opens the dialog for adding a new prescription.
     */
    private void openAddPrescriptionDialog() {
        AddPrescriptionDialog addPrescriptionDialog = new AddPrescriptionDialog(patients, diseases, medications, prescriptionService, prescriptionTable);
        addPrescriptionDialog.show();
    }
}