package com.ace.ucv.manage;

import com.ace.ucv.model.Patient;
import com.ace.ucv.services.interfaces.IPatientService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class EditPatientDialog {
    private final IPatientService patientService;
    private final TableView<Patient> patientTableView;

    /**
     * Constructor for EditPatientDialog.
     * Initializes the dialog with patient service and the table view for patients.
     *
     * @param patientService Service for handling patient-related operations.
     * @param patientTableView TableView for displaying patients.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EditPatientDialog(IPatientService patientService, TableView<Patient> patientTableView) {
        this.patientService = patientService;
        this.patientTableView = patientTableView;
    }


    public void showEditPatientDialog(Patient patient) {
        Dialog<Patient> dialog = new Dialog<>();
        establishPatientModalInformation(dialog);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        setGridDimension(editGrid);

        TextField editNameField = new TextField(patient.getName());
        TextField editAgeField = new TextField(String.valueOf(patient.getAge()));
        TextField editFieldOfWorkField = new TextField(patient.getFieldOfWork());

        editGridInformation(editGrid, editNameField, editAgeField, editFieldOfWorkField);
        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        establishFieldsInformation(editNameField, saveButton, editAgeField, editFieldOfWorkField);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                buildResult(patient, editNameField, editAgeField, editFieldOfWorkField);
            }
            return null;
        });
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialog.showAndWait();
    }

    private void establishFieldsInformation(TextField editNameField, Node saveButton, TextField editAgeField, TextField editFieldOfWorkField) {
        // Establishes listeners for text fields to enable or disable the save button
        editNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));
        editAgeField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));
        editFieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));
    }

    private static void establishPatientModalInformation(Dialog<Patient> dialog) {
        // Sets up basic information for the patient modal
        dialog.setTitle("Edit Patient");
        dialog.setHeaderText("Edit patient information:");
    }

    private void buildResult(Patient patient, TextField editNameField, TextField editAgeField, TextField editFieldOfWorkField) {
        // Builds and returns the edited patient data
        String editedName = editNameField.getText();
        int editedAge = Integer.parseInt(editAgeField.getText());
        String editedFieldOfWork = editFieldOfWorkField.getText();

        // Edit the patient and refresh the table view
        patientService.editPatient(patient, editedName, editedAge, editedFieldOfWork);
        patientTableView.refresh();
    }

    private static void setGridDimension(GridPane editGrid) {
        // Sets dimensions for the edit grid
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
    }

    private static void editGridInformation(GridPane editGrid, TextField editNameField, TextField editAgeField, TextField editFieldOfWorkField) {
        // Adds information to the edit grid
        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);
        editGrid.add(new Label("Age:"), 0, 1);
        editGrid.add(editAgeField, 1, 1);
        editGrid.add(new Label("Field of Work:"), 0, 2);
        editGrid.add(editFieldOfWorkField, 1, 2);
    }

    private void updateButtonState(Node saveButton, TextField editNameField, TextField editAgeField, TextField editFieldOfWorkField) {
        String editedName = editNameField.getText().trim();
        String editedAge = editAgeField.getText().trim();
        String editedFieldOfWork = editFieldOfWorkField.getText().trim();

        boolean isValid = !editedName.isEmpty() && editedName.matches("[a-zA-Z ]+")
                && !editedAge.isEmpty() && editedAge.matches("\\d+")
                && !editedFieldOfWork.isEmpty() && editedFieldOfWork.matches("[a-zA-Z ]+");

        saveButton.setDisable(!isValid);
    }
}