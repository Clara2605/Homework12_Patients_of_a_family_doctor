package com.ace.ucv.manage;

import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.PrescriptionService;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class ManagePrescription {
    private static final Logger logger = LogManager.getLogger(ManagePrescription.class);
    private Stage primaryStage;
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

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.prescriptionService = new PrescriptionService();
        this.patientService = new PatientService();
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = prescriptionService.loadItemsFromDatabase("diseases", "name");
        this.medications = prescriptionService.loadItemsFromDatabase("medications", "name");
        this.prescriptionTable = new TableView<>();

        addPrescriptionButton = new Button("Add Prescription");
        addPrescriptionButton.setOnAction(e -> openAddPrescriptionDialog());

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        setupButtons();
        prescriptions = FXCollections.observableArrayList();
        prescriptions.addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }

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

    private void editSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(selectedPrescription, prescriptionService, prescriptionTable);
            editPrescriptionDialog.show();
        }
    }

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

    public void start() {
        loadPatientsFromDatabase();
        setupPrescriptionTable();
        loadPrescriptionsFromDatabase();
    }

    private void setupPrescriptionTable() {
        TableColumn<Prescription, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Prescription, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Prescription, String> diseaseColumn = new TableColumn<>("Disease");
        diseaseColumn.setCellValueFactory(new PropertyValueFactory<>("disease"));

        TableColumn<Prescription, String> medicationColumn = new TableColumn<>("Medication");
        medicationColumn.setCellValueFactory(new PropertyValueFactory<>("medication"));

        TableColumn<Prescription, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Prescription, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");{
                // Set up buttons (actions, styles)
                setupEditButton(editButton);
                setupDeleteButton(deleteButton);
            }

            private void setupEditButton(Button button) {
                button.setOnAction(e -> {
                    Prescription selectedPrescription = (Prescription) getTableView().getItems().get(getIndex());
                    EditPrescriptionDialog editPrescriptionDialog = new EditPrescriptionDialog(selectedPrescription, prescriptionService, prescriptionTable);
                    editPrescriptionDialog.show();
                });
            }

            private void setupDeleteButton(Button button) {
                button.setOnAction(e -> {
                    Prescription selectedPrescription = (Prescription) getTableView().getItems().get(getIndex());
                    prescriptionService.deletePrescription(selectedPrescription.getId());
                    prescriptions.remove(selectedPrescription);
                });
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
        });

        prescriptionTable.getColumns().addAll(idColumn, dateColumn, diseaseColumn, medicationColumn, actionsColumn);

        HBox buttonBox = new HBox(addPrescriptionButton);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        VBox container = new VBox(buttonBox, prescriptionTable);
        container.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(container, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPatientsFromDatabase() {
        try {
            List<Patient> patientsList = patientService.loadPatientsFromDatabase(); // Use PatientService to load patients
            patients.clear();
            patients.addAll(patientsList);
        } catch (Exception e) {
            logger.error(String.format("Error loading patients from the database: %s", e.getMessage()));
        }
    }

    private void loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();
        prescriptionTable.setItems(prescriptions);
    }

    private void openAddPrescriptionDialog() {
        AddPrescriptionDialog addPrescriptionDialog = new AddPrescriptionDialog(patients, diseases, medications, prescriptionService, prescriptionTable);
        addPrescriptionDialog.show();
    }
}