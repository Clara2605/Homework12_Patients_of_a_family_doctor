package com.ace.ucv.manage;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class EditPrescriptionDialog {
    private Prescription prescription;
    private IPrescriptionService prescriptionService;
    private TableView<Prescription> prescriptionTable;
    private ObservableList<Patient> patients;
    private List<String> diseases;
    private List<String> medications;
    private TextField ageTextField;
    private static final Logger logger = LogManager.getLogger(EditPrescriptionDialog.class);

    /**
     * Constructor for EditPrescriptionDialog.
     * Initializes the dialog with the existing prescription, prescription service, table view for prescriptions,
     * list of patients, diseases, and medications.
     *
     * @param prescription The prescription to be edited.
     * @param prescriptionService Service for handling prescription-related operations.
     * @param prescriptionTable TableView for displaying prescriptions.
     * @param patients ObservableList of Patients to be used in the prescription.
     * @param diseases List of disease names.
     * @param medications List of medication names.
     */
    public EditPrescriptionDialog(Prescription prescription, IPrescriptionService prescriptionService, TableView<Prescription> prescriptionTable, ObservableList<Patient> patients, List<String> diseases, List<String> medications) {
        this.prescription = prescription;
        this.prescriptionService = prescriptionService;
        this.prescriptionTable = prescriptionTable;
        this.patients = patients;
        this.diseases = diseases;
        this.medications = medications;
        this.ageTextField = new TextField();
    }

    /**
     * Displays the dialog for editing an existing prescription.
     * Users can update prescription details and save them to the database.
     */
    public void show() {
        Dialog<Void> dialog = createDialog();
        dialog.showAndWait();
    }

    private Dialog<Void> createDialog() {
        // Creates the dialog layout for editing a prescription.
        // Sets up the form fields, buttons, and styles for the dialog.
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Prescription");
        dialog.setHeaderText("Edit a prescription:");

        GridPane prescriptionGrid = createPrescriptionGrid();
        dialog.getDialogPane().setContent(prescriptionGrid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        setupFormValidation(prescriptionGrid, saveButton);
        setupSaveAction(dialog, saveButtonType, prescriptionGrid);
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        return dialog;
    }

    private GridPane createPrescriptionGrid() {
        GridPane prescriptionGrid = initializeGridPane();

        DatePicker dateField = createDateField();
        ComboBox<Patient> patientComboBox = createPatientComboBox();
        ComboBox<String> diseaseComboBox = createDiseaseComboBox();
        ComboBox<String> medicationComboBox = createMedicationComboBox();

        dateField.setValue(LocalDate.parse(prescription.getDate()));

        patientComboBox.setValue(findPatientById(prescription.getPatientId()));
        patientComboBox.getSelectionModel().select(findPatientById(prescription.getPatientId()));
        Patient patient = findPatientById(prescription.getPatientId());
        if (patient != null) {
            patientComboBox.setValue(patient);
        } else {
            logger.warn("The selected patient could not be set in the ComboBox.");
        }
        diseaseComboBox.setValue(prescription.getDisease());
        medicationComboBox.setValue(prescription.getMedication());

        addGridContent(prescriptionGrid, dateField, patientComboBox, diseaseComboBox, medicationComboBox);

        return prescriptionGrid;
    }

    private GridPane initializeGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        return grid;
    }

    private DatePicker createDateField() {
        return new DatePicker();
    }

    private ComboBox<Patient> createPatientComboBox() {
        // Creates the combo box for selecting a patient.
        // Sets up a StringConverter to display patient names and handles patient selection changes.
        ComboBox<Patient> comboBox = new ComboBox<>(patients);
        if (patients != null && !patients.isEmpty()) {
            comboBox.setItems(patients); // Set the patients in the ComboBox
        } else {
            logger.warn("The patient list is empty or not loaded correctly.");
        }
        comboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient != null ? patient.getName() : "";
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });

        newValueInComboBox(comboBox);
        return comboBox;
    }

    private void newValueInComboBox(ComboBox<Patient> comboBox) {
        // Adds a listener to the patient combo box to update the age text field when a new patient is selected.
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ageTextField.setText(String.valueOf(newValue.getAge()));
            } else {
                ageTextField.clear();
            }
        });
    }

    private ComboBox<String> createDiseaseComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(diseases);
        return comboBox;
    }

    private ComboBox<String> createMedicationComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(medications);
        return comboBox;
    }

    private void addGridContent(GridPane grid, DatePicker dateField, ComboBox<Patient> patientComboBox, ComboBox<String> diseaseComboBox, ComboBox<String> medicationComboBox) {
        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Patient:"), 0, 1);
        grid.add(patientComboBox, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageTextField, 1, 2); // Assuming ageTextField is a class member
        grid.add(new Label("Disease:"), 0, 3);
        grid.add(diseaseComboBox, 1, 3);
        grid.add(new Label("Medication:"), 0, 4);
        grid.add(medicationComboBox, 1, 4);
    }

    private void setupFormValidation(GridPane prescriptionGrid, Node saveButton) {
        // Sets up form validation for the prescription form.
        // Enables or disables the save button based on the validity of form data.
        DatePicker dateField = (DatePicker) prescriptionGrid.getChildren().get(1);
        ComboBox<Patient> patientComboBox = (ComboBox<Patient>) prescriptionGrid.getChildren().get(3);
        ComboBox<String> diseaseComboBox = (ComboBox<String>) prescriptionGrid.getChildren().get(7);
        ComboBox<String> medicationComboBox = (ComboBox<String>) prescriptionGrid.getChildren().get(9);

        dateField.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        patientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        diseaseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        medicationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
    }

    private void setupSaveAction(Dialog<Void> dialog, ButtonType saveButtonType, GridPane prescriptionGrid) {
        // Sets up the save action for the dialog.
        // Builds the edited prescription data and stores it using the prescription service.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                DatePicker dateField = (DatePicker) prescriptionGrid.getChildren().get(1);
                ComboBox<Patient> patientComboBox = (ComboBox<Patient>) prescriptionGrid.getChildren().get(3);
                ComboBox<String> diseaseComboBox = (ComboBox<String>) prescriptionGrid.getChildren().get(7);
                ComboBox<String> medicationComboBox = (ComboBox<String>) prescriptionGrid.getChildren().get(9);

                String date = String.valueOf(dateField.getValue());
                Patient selectedPatient = patientComboBox.getValue();
                String diseaseName = diseaseComboBox.getValue();
                String medicationName = medicationComboBox.getValue();

                if (date != null && selectedPatient != null && diseaseName != null && medicationName != null) {
                    storeData(prescription.getId(), diseaseName, medicationName, selectedPatient, date);
                }
            }
            return null;
        });
    }

    private void storeData(int prescriptionId, String diseaseName, String medicationName, Patient selectedPatient, String date) {
        // Stores the edited prescription data by interacting with the prescription service.
        // Updates the UI based on the success or failure of the operation.
        int diseaseId = prescriptionService.getIdFromName("diseases", diseaseName);
        int medicationId = prescriptionService.getIdFromName("medications", medicationName);

        if (diseaseId != -1 && medicationId != -1) {
            boolean success = prescriptionService.editPrescription(prescriptionId, date, selectedPatient.getId(), diseaseId, medicationId);
            if (success) {
                refreshTable();
                showAlert("Prescription Updated", "The prescription was updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Prescription Update Failed", "There was a problem updating the prescription.", Alert.AlertType.ERROR);
            }
        }
    }

    private Patient findPatientById(int patientId) {
        // Implement logic to find a patient by ID from 'patients' ObservableList
        return patients.stream().filter(p -> p.getId() == patientId).findFirst().orElse(null);
    }

    private void refreshTable() {
        prescriptionTable.getItems().clear();
        prescriptionTable.getItems().addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void validateForm(DatePicker dateField, ComboBox<Patient> patientComboBox, ComboBox<String> diseaseComboBox, ComboBox<String> medicationComboBox, Node saveButton) {
        LocalDate selectedDate = dateField.getValue();
        boolean isDateValid = selectedDate != null && !selectedDate.isAfter(LocalDate.now());
        boolean isPatientSelected = patientComboBox.getValue() != null;
        boolean isDiseaseSelected = diseaseComboBox.getValue() != null;
        boolean isMedicationSelected = medicationComboBox.getValue() != null;

        saveButton.setDisable(!(isDateValid && isPatientSelected && isDiseaseSelected && isMedicationSelected));
    }
}
