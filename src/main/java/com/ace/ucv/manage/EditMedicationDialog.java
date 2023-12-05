package com.ace.ucv.manage;

import com.ace.ucv.model.Medication;
import com.ace.ucv.services.interfaces.IMedicationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class EditMedicationDialog {
    private final IMedicationService medicationService;
    private final TableView<Medication> medicationTableView;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EditMedicationDialog(IMedicationService medicationService, TableView<Medication> medicationTableView) {
        this.medicationService = medicationService;
        this.medicationTableView = medicationTableView;
    }

    public void showEditMedicationDialog(Medication medication) {
        Dialog<Medication> dialog = new Dialog<>();
        dialog.setTitle("Edit Medication");
        dialog.setHeaderText("Edit medication information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(medication.getName());
        TextField editCategoryField = new TextField(medication.getCategory());

        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);
        editGrid.add(new Label("Category:"), 0, 1);
        editGrid.add(editCategoryField, 1, 1);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> updateEditButtonState(saveButton, editNameField, editCategoryField));

        editCategoryField.textProperty().addListener((observable, oldValue, newValue) -> updateEditButtonState(saveButton, editNameField, editCategoryField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                String editedCategory = editCategoryField.getText();

                medicationService.editMedication(medication, editedName, editedCategory);
                medicationTableView.refresh();
            }
            return null;
        });
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.showAndWait();
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField, TextField editCategoryField) {
        String editedName = editNameField.getText().trim();
        String editedCategory = editCategoryField.getText().trim();
        boolean isValid = !editedName.isEmpty() && !editedCategory.isEmpty();
        saveButton.setDisable(!isValid);
    }
}
