package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddPrescription {
    private Stage primaryStage;
    private Patient selectedPatient;
    private ObservableList<Patient> patients;

    public AddPrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
    }

    public void start() {
        // Aici puteți afișa fereastra de dialog pentru adăugarea rețetei.
        // Restul codului rămâne neschimbat din versiunea anterioară.
        showAddPrescriptionDialog(selectedPatient);
    }

    public void showAddPrescriptionDialog(Patient patient) {
        this.selectedPatient = patient;

        Dialog<PrescriptionData> dialog = new Dialog<>();
        dialog.setTitle("Add Prescription");
        dialog.setHeaderText("Add a new prescription:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane prescriptionGrid = new GridPane();
        prescriptionGrid.setHgap(10);
        prescriptionGrid.setVgap(10);
        prescriptionGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        DatePicker dateField = new DatePicker();
        ComboBox<Disease> diseaseComboBox = new ComboBox<>();
        ComboBox<Medication> medicationComboBox = new ComboBox<>();

        // Populați combobox-urile cu datele adecvate (disease și medication)

        prescriptionGrid.add(new Label("Date:"), 0, 0);
        prescriptionGrid.add(dateField, 1, 0);
        prescriptionGrid.add(new Label("Disease:"), 0, 1);
        prescriptionGrid.add(diseaseComboBox, 1, 1);
        prescriptionGrid.add(new Label("Medication:"), 0, 2);
        prescriptionGrid.add(medicationComboBox, 1, 2);

        dialog.getDialogPane().setContent(prescriptionGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || diseaseComboBox.getValue() == null || medicationComboBox.getValue() == null);
        });

        diseaseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || dateField.getValue() == null || medicationComboBox.getValue() == null);
        });

        medicationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || dateField.getValue() == null || diseaseComboBox.getValue() == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                LocalDate date = dateField.getValue();
                Disease disease = diseaseComboBox.getValue();
                Medication medication = medicationComboBox.getValue();

                if (date != null && disease != null && medication != null) {
                    savePrescription(selectedPatient, date, disease, medication);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void savePrescription(Patient patient, LocalDate date, Disease disease, Medication medication) {
        int patientId = getPatientIdFromDatabase(patient.getName());

        if (patientId != -1) {
            try (Connection connection = DatabaseManager.connect();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, date.toString());
                preparedStatement.setInt(2, patientId);
                preparedStatement.setInt(3, disease.getId());
                preparedStatement.setInt(4, medication.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Pacientul nu a fost găsit în baza de date.");
        }
    }

    private int getPatientIdFromDatabase(String patientName) {
        int patientId = -1;
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM patients WHERE name = ?")) {
            preparedStatement.setString(1, patientName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                patientId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientId;
    }

    public class PrescriptionData {
        private LocalDate date;
        private Disease disease;
        private Medication medication;

        public PrescriptionData(LocalDate date, Disease disease, Medication medication) {
            this.date = date;
            this.disease = disease;
            this.medication = medication;
        }

        public LocalDate getDate() {
            return date;
        }

        public Disease getDisease() {
            return disease;
        }

        public Medication getMedication() {
            return medication;
        }
    }
}
