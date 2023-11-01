package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.services.PrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManagePrescription {
    private Stage primaryStage;
    private ObservableList<Patient> patients;
    private List<String> diseases;
    private List<String> medications;
    private ComboBox<Patient> patientComboBox;
    private TextField ageTextField;
    private TableView<Prescription> prescriptionTable;
    private PrescriptionService prescriptionService;

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.prescriptionService = new PrescriptionService();
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = prescriptionService.loadItemsFromDatabase("diseases", "name");
        this.medications = prescriptionService.loadItemsFromDatabase("medications", "name");
        this.prescriptionTable = new TableView<>();

    }

    public void start() {
        loadPatientsFromDatabase();
        setupPrescriptionTable(); // Adaugă această linie
        showAddPrescriptionDialog();
        loadPrescriptionsFromDatabase();
    }


    public void showAddPrescriptionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Prescription");
        dialog.setHeaderText("Add a new prescription:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane prescriptionGrid = new GridPane();
        prescriptionGrid.setHgap(10);
        prescriptionGrid.setVgap(10);
        prescriptionGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        DatePicker dateField = new DatePicker();
        patientComboBox = new ComboBox<>(patients);
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient != null ? patient.getName() : "";
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });

        patientComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ageTextField.setText(String.valueOf(newValue.getAge()));
            } else {
                ageTextField.clear();
            }
        });

        ageTextField = new TextField();
        ComboBox<String> diseaseComboBox = new ComboBox<>();
        diseaseComboBox.getItems().addAll(diseases);

        ComboBox<String> medicationComboBox = new ComboBox<>();
        medicationComboBox.getItems().addAll(medications);

        prescriptionGrid.add(new Label("Date:"), 0, 0);
        prescriptionGrid.add(dateField, 1, 0);
        prescriptionGrid.add(new Label("Patient:"), 0, 1);
        prescriptionGrid.add(patientComboBox, 1, 1);
        prescriptionGrid.add(new Label("Age:"), 0, 2);
        prescriptionGrid.add(ageTextField, 1, 2);
        prescriptionGrid.add(new Label("Disease:"), 0, 3);
        prescriptionGrid.add(diseaseComboBox, 1, 3);
        prescriptionGrid.add(new Label("Medication:"), 0, 4);
        prescriptionGrid.add(medicationComboBox, 1, 4);

        dialog.getDialogPane().setContent(prescriptionGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        dateField.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        patientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        diseaseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));
        medicationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(dateField, patientComboBox, diseaseComboBox, medicationComboBox, saveButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String date = String.valueOf(dateField.getValue());
                Patient selectedPatient = patientComboBox.getValue();
                String diseaseName = diseaseComboBox.getValue();
                String medicationName = medicationComboBox.getValue();

                if (date != null && selectedPatient != null && diseaseName != null && medicationName != null) {
                    int diseaseId = prescriptionService.getIdFromName("diseases", diseaseName);
                    int medicationId = prescriptionService.getIdFromName("medications", medicationName);

                    if (diseaseId != -1 && medicationId != -1) {
                        savePrescription(selectedPatient, date, String.valueOf(diseaseId), String.valueOf(medicationId));
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void setupPrescriptionTable() {
        TableColumn<Prescription, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Prescription, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Prescription, String> diseaseColumn = new TableColumn<>("Disease");
        diseaseColumn.setCellValueFactory(new PropertyValueFactory<>("disease"));

        TableColumn<Prescription, String> medicationColumn = new TableColumn<>("Medication");
        medicationColumn.setCellValueFactory(new PropertyValueFactory<>("medication"));

        prescriptionTable.getColumns().addAll(idColumn, dateColumn, diseaseColumn, medicationColumn);

        VBox container = new VBox(prescriptionTable);
        container.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(container, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void savePrescription(Patient patient, String date, String diseaseId, String medicationId) {
        int patientId = patient.getId();

        if (Integer.valueOf(diseaseId) != -1 && Integer.valueOf(medicationId) != -1) {
            try (Connection connection = DatabaseManager.connect()) {
                connection.setAutoCommit(false);

                String insertPrescriptionSQL = "INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPrescriptionSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, date);
                    preparedStatement.setInt(2, patientId);
                    preparedStatement.setString(3, diseaseId);
                    preparedStatement.setString(4, medicationId);
                    preparedStatement.executeUpdate();

                    connection.commit();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int prescriptionId = generatedKeys.getInt(1);
                        Prescription prescription = new Prescription(prescriptionId, date, diseaseId, medicationId);
                        prescriptionTable.getItems().add(prescription);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPatientsFromDatabase() {
        patients.clear();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, age, field_of_work FROM patients");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String fieldOfWork = resultSet.getString("field_of_work");

                patients.add(new Patient(id, name, age, fieldOfWork));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();
        prescriptionTable.setItems(prescriptions);
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