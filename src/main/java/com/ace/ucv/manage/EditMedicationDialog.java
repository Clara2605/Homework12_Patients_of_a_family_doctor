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
    private static final String STYLE_SHEET_PATH = "/css/style.css";

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EditMedicationDialog(IMedicationService medicationService, TableView<Medication> medicationTableView) {
        this.medicationService = medicationService;
        this.medicationTableView = medicationTableView;
    }

    public void showEditMedicationDialog(Medication medication) {
        Dialog<Medication> dialog = new Dialog<>();
        setupDialogBasics(dialog);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = setupEditGrid(medication);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Event listeners for text field changes
        addTextChangeListeners(saveButton, editGrid);

        dialog.setResultConverter(dialogButton -> (Medication) processDialogResult(dialogButton, saveButtonType, editGrid, medication));

        dialog.getDialogPane().getStylesheets().add(getClass().getResource(STYLE_SHEET_PATH).toExternalForm());
        dialog.showAndWait();
    }

    private void setupDialogBasics(Dialog<Medication> dialog) {
        dialog.setTitle("Edit Medication");
        dialog.setHeaderText("Edit medication information:");
    }

    private GridPane setupEditGrid(Medication medication) {
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

        return editGrid;
    }

    private void addTextChangeListeners(Node saveButton, GridPane editGrid) {
        TextField editNameField = (TextField) editGrid.getChildren().get(1);
        TextField editCategoryField = (TextField) editGrid.getChildren().get(3);

        editNameField.textProperty().addListener((observable, oldValue, newValue) ->
                updateEditButtonState(saveButton, editNameField, editCategoryField));

        editCategoryField.textProperty().addListener((observable, oldValue, newValue) ->
                updateEditButtonState(saveButton, editNameField, editCategoryField));
    }

    private Object processDialogResult(ButtonType dialogButton, ButtonType saveButtonType, GridPane editGrid, Medication medication) {
        if (dialogButton == saveButtonType) {
            TextField editNameField = (TextField) editGrid.getChildren().get(1);
            TextField editCategoryField = (TextField) editGrid.getChildren().get(3);

            String editedName = editNameField.getText();
            String editedCategory = editCategoryField.getText();

            medicationService.editMedication(medication, editedName, editedCategory);
            medicationTableView.refresh();
            return medication;  // Return the modified medication
        }
        return null;
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField, TextField editCategoryField) {
        String editedName = editNameField.getText().trim();
        String editedCategory = editCategoryField.getText().trim();
        boolean isValid = !editedName.isEmpty() && !editedCategory.isEmpty();
        saveButton.setDisable(!isValid);
    }
}
