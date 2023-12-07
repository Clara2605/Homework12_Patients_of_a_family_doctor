package com.ace.ucv.manage;

import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;

import java.util.Optional;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class EditPrescriptionDialog {
    private Prescription prescription;
    private IPrescriptionService prescriptionService;
    private TableView<Prescription> prescriptionTable;

    /**
     * Constructor for EditPrescriptionDialog.
     * Initializes the dialog with the prescription to be edited, the prescription service, and the table view.
     *
     * @param prescription The prescription to be edited.
     * @param prescriptionService Service for handling prescription-related operations.
     * @param prescriptionTable TableView for displaying prescriptions.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EditPrescriptionDialog(Prescription prescription, IPrescriptionService prescriptionService, TableView<Prescription> prescriptionTable) {
        this.prescription = prescription;
        this.prescriptionService = prescriptionService;
        this.prescriptionTable = prescriptionTable;
    }

    public void show() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Prescription");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createGridPane();

        TextField dateField = new TextField();
        dateField.setPromptText("Date");
        dateField.setText(prescription.getDate());

        TextField diseaseIdField = new TextField();
        diseaseIdField.setPromptText("Disease ID");
        diseaseIdField.setText(String.valueOf(prescription.getDiseaseId()));

        TextField medicationIdField = new TextField();
        medicationIdField.setPromptText("Medication ID");
        medicationIdField.setText(String.valueOf(prescription.getMedicationId()));

        setGridContext(grid, dateField, diseaseIdField, medicationIdField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> dialogButton == saveButtonType ? new Pair<>(dateField.getText(), diseaseIdField.getText() + ";" + medicationIdField.getText()) : null);

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(this::isPresent);
    }

    /**
     * Processes the result when the dialog is closed and the save button is clicked.
     * Updates the prescription data and refreshes the table view.
     *
     * @param pair Pair containing the updated date and concatenated IDs.
     */
    private void isPresent(Pair<String, String> pair) {
        String date = pair.getKey();
        String[] ids = pair.getValue().split(";");
        int patientId = Integer.parseInt(ids[0]);
        int diseaseId = Integer.parseInt(ids[1]);
        int medicationId = Integer.parseInt(ids[2]);
        prescriptionService.editPrescription(prescription.getId(), date, patientId, diseaseId, medicationId);
        refreshTable();
    }

    /**
     * Sets up the grid layout for the edit dialog, adding labels and fields.
     *
     * @param grid The GridPane to be configured.
     * @param dateField TextField for editing the date.
     * @param diseaseIdField TextField for editing the disease ID.
     * @param medicationIdField TextField for editing the medication ID.
     */
    private static void setGridContext(GridPane grid, TextField dateField, TextField diseaseIdField, TextField medicationIdField) {
        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Disease ID:"), 0, 1);
        grid.add(diseaseIdField, 1, 1);
        grid.add(new Label("Medication ID:"), 0, 2);
        grid.add(medicationIdField, 1, 2);
    }


    /**
     * Creates and configures a GridPane for the edit dialog.
     *
     * @return A configured GridPane.
     */
    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    /**
     * Refreshes the prescription table with updated data from the database.
     */
    private void refreshTable() {
        prescriptionTable.getItems().clear();
        prescriptionTable.getItems().addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }
}
