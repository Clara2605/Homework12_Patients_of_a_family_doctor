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

        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Disease ID:"), 0, 1);
        grid.add(diseaseIdField, 1, 1);
        grid.add(new Label("Medication ID:"), 0, 2);
        grid.add(medicationIdField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Pair<>(dateField.getText(), diseaseIdField.getText() + ";" + medicationIdField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String date = pair.getKey();
            String[] ids = pair.getValue().split(";");
            int patientId = Integer.parseInt(ids[0]);
            int diseaseId = Integer.parseInt(ids[1]);
            int medicationId = Integer.parseInt(ids[2]);
            prescriptionService.editPrescription(prescription.getId(), date, patientId, diseaseId, medicationId);
            refreshTable();
        });
    }


    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private void refreshTable() {
        prescriptionTable.getItems().clear();
        prescriptionTable.getItems().addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }
}
