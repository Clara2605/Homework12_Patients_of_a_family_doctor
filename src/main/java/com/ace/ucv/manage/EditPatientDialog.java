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

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EditPatientDialog(IPatientService patientService, TableView<Patient> patientTableView) {
        this.patientService = patientService;
        this.patientTableView = patientTableView;
    }

    public void showEditPatientDialog(Patient patient) {
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle("Edit Patient");
        dialog.setHeaderText("Edit patient information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(patient.getName());
        TextField editAgeField = new TextField(String.valueOf(patient.getAge()));
        TextField editFieldOfWorkField = new TextField(patient.getFieldOfWork());

        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);
        editGrid.add(new Label("Age:"), 0, 1);
        editGrid.add(editAgeField, 1, 1);
        editGrid.add(new Label("Field of Work:"), 0, 2);
        editGrid.add(editFieldOfWorkField, 1, 2);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));

        editAgeField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));

        editFieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                int editedAge = Integer.parseInt(editAgeField.getText());
                String editedFieldOfWork = editFieldOfWorkField.getText();

                patientService.editPatient(patient, editedName, editedAge, editedFieldOfWork);
                patientTableView.refresh();
            }
            return null;
        });
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialog.showAndWait();
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
