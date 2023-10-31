package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = loadDiseasesFromDatabase();
        this.medications = loadMedicationsFromDatabase();
    }

    public void start() {
        loadPatientsFromDatabase();
        showAddPrescriptionDialog();
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

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || patientComboBox.getValue() == null || diseaseComboBox.getValue() == null || medicationComboBox.getValue() == null);
        });

        patientComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || dateField.getValue() == null || diseaseComboBox.getValue() == null || medicationComboBox.getValue() == null);
        });

        diseaseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || dateField.getValue() == null || patientComboBox.getValue() == null || medicationComboBox.getValue() == null);
        });

        medicationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || dateField.getValue() == null || patientComboBox.getValue() == null || diseaseComboBox.getValue() == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                LocalDate date = dateField.getValue();
                Patient selectedPatient = patientComboBox.getValue();
                String disease = diseaseComboBox.getValue();
                String medication = medicationComboBox.getValue();

                if (date != null && selectedPatient != null && disease != null && medication != null) {
                    savePrescription(selectedPatient, date, disease, medication);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void savePrescription(Patient patient, LocalDate date, String disease, String medication) {
        int patientId = patient.getId();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO prescriptions (date, patient_id, disease, medication) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, date.toString());
            preparedStatement.setInt(2, patientId);
            preparedStatement.setString(3, disease);
            preparedStatement.setString(4, medication);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadDiseasesFromDatabase() {
        List<String> diseases = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM diseases");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                diseases.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diseases;
    }

    private List<String> loadMedicationsFromDatabase() {
        List<String> medications = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM medications");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                medications.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medications;
    }

    private void loadPatientsFromDatabase() {
        patients.clear(); // Curăță lista existentă (dacă există)

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
}
