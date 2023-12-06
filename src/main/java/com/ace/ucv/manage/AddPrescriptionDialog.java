package com.ace.ucv.manage;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.List;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP2", "EI_EXPOSE_REP2"})
public class AddPrescriptionDialog {
    private ObservableList<Patient> patients;
    private List<String> diseases;
    private List<String> medications;
    private IPrescriptionService prescriptionService;
    private TableView<Prescription> prescriptionTable;
    private TextField ageTextField;

    public AddPrescriptionDialog(ObservableList<Patient> patients, List<String> diseases, List<String> medications, IPrescriptionService prescriptionService, TableView<Prescription> prescriptionTable) {
        this.patients = patients;
        this.diseases = diseases;
        this.medications = medications;
        this.prescriptionService = prescriptionService;
        this.prescriptionTable = prescriptionTable;
        this.ageTextField = new TextField();
    }

    public void show() {
        Dialog<Void> dialog = createDialog();
        dialog.showAndWait();
    }

    private Dialog<Void> createDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Prescription");
        dialog.setHeaderText("Add a new prescription:");

        GridPane prescriptionGrid = createPrescriptionGrid();
        dialog.getDialogPane().setContent(prescriptionGrid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        setupFormValidation(prescriptionGrid, saveButton);
        setupSaveAction(dialog, saveButtonType, prescriptionGrid);

        return dialog;
    }

    private GridPane createPrescriptionGrid() {
        GridPane prescriptionGrid = initializeGridPane();

        DatePicker dateField = createDateField();
        ComboBox<Patient> patientComboBox = createPatientComboBox();
        ComboBox<String> diseaseComboBox = createDiseaseComboBox();
        ComboBox<String> medicationComboBox = createMedicationComboBox();

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
        ComboBox<Patient> comboBox = new ComboBox<>(patients);
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

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ageTextField.setText(String.valueOf(newValue.getAge()));
            } else {
                ageTextField.clear();
            }
        });
        return comboBox;
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
                    storeData(diseaseName, medicationName, selectedPatient, date);
                }
            }
            return null;
        });
    }

    private void storeData(String diseaseName, String medicationName, Patient selectedPatient, String date) {
        int diseaseId = prescriptionService.getIdFromName("diseases", diseaseName);
        int medicationId = prescriptionService.getIdFromName("medications", medicationName);

        if (diseaseId != -1 && medicationId != -1) {
            boolean success = prescriptionService.savePrescription(selectedPatient, date, String.valueOf(diseaseId), String.valueOf(medicationId));
            if (success) {
                Prescription newPrescription = new Prescription(selectedPatient.getId(), date, diseaseName, medicationName);
                // Actualizarea listei de prescripții din TableView
                prescriptionTable.getItems().add(newPrescription);
                showAlert("Prescription Saved", "The prescription was saved successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Prescription Saving Failed", "There was a problem saving the prescription.", Alert.AlertType.ERROR);
            }
        }
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
