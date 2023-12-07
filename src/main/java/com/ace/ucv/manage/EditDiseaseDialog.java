package com.ace.ucv.manage;

import com.ace.ucv.model.Disease;
import com.ace.ucv.services.interfaces.IDiseaseService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class EditDiseaseDialog {
    private final IDiseaseService diseaseService;
    private final TableView<Disease> diseaseTableView;

    /**
     * Constructor for EditDiseaseDialog.
     * Initializes the dialog with the disease service and the table view where diseases are displayed.
     *
     * @param diseaseService Service for handling disease-related operations.
     * @param diseaseTableView TableView for displaying diseases.
     */
    public EditDiseaseDialog(IDiseaseService diseaseService, TableView<Disease> diseaseTableView) {
        this.diseaseService = diseaseService;
        this.diseaseTableView = diseaseTableView;
    }

    public void showEditDiseaseDialog(Disease disease) {
        Dialog<Disease> dialog = createEditDiseaseDialog();

        Node saveButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.setDisable(true);

        TextField editNameField = createEditNameField(disease, saveButton);

        dialog.getDialogPane().setContent(new GridPane() {{
            setHgap(10);
            setVgap(10);
            setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            add(new Label("Name:"), 0, 0);
            add(editNameField, 1, 0);
        }});

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String editedName = editNameField.getText().trim();
                diseaseService.editDisease(disease, editedName);
                diseaseTableView.refresh();
            }
            return null;
        });

        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialog.showAndWait();
    }

    /**
     * Creates and configures the dialog for editing a disease.
     * Sets up the dialog layout and buttons.
     *
     * @return A Dialog object configured for editing a disease.
     */
    private Dialog<Disease> createEditDiseaseDialog() {
        Dialog<Disease> dialog = new Dialog<>();
        dialog.setTitle("Edit Disease");
        dialog.setHeaderText("Edit disease information:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        return dialog;
    }

    /**
     * Creates and configures the text field for editing the name of a disease.
     * Adds a listener to validate the input and enable/disable the save button accordingly.
     *
     * @param disease The disease being edited.
     * @param saveButton The save button in the dialog.
     * @return A TextField pre-filled with the disease's current name.
     */
    private TextField createEditNameField(Disease disease, Node saveButton) {
        TextField editNameField = new TextField(disease.getName());

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            String editedName = editNameField.getText().trim();
            boolean isValid = !editedName.isEmpty() && editedName.matches("[a-zA-Z ]+");
            saveButton.setDisable(!isValid);
        });

        return editNameField;
    }
}
