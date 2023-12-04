package com.ace.ucv.manage;

import com.ace.ucv.model.Disease;
import com.ace.ucv.services.interfaces.IDiseaseService;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class EditDiseaseDialog {
    private final IDiseaseService diseaseService;
    private final TableView<Disease> diseaseTableView;

    public EditDiseaseDialog(IDiseaseService diseaseService, TableView<Disease> diseaseTableView) {
        this.diseaseService = diseaseService;
        this.diseaseTableView = diseaseTableView;
    }

    public void showEditDiseaseDialog(Disease disease) {
        Dialog<Disease> dialog = new Dialog<>();
        dialog.setTitle("Edit Disease");
        dialog.setHeaderText("Edit disease information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(disease.getName());
        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                diseaseService.editDisease(disease, editedName);
                diseaseTableView.refresh();
            }
            return null;
        });
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialog.showAndWait();
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField) {
        String editedName = editNameField.getText().trim();
        boolean isValid = !editedName.isEmpty() && editedName.matches("[a-zA-Z ]+");
        saveButton.setDisable(!isValid);
    }
}
