package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
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

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = loadItemsFromDatabase("diseases", "name");
        this.medications = loadItemsFromDatabase("medications", "name");
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
                    int diseaseId = getIdFromName("diseases", diseaseName);
                    int medicationId = getIdFromName("medications", medicationName);

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

        // Adăugă tabelul într-un container (VBox în acest exemplu)
        VBox container = new VBox(prescriptionTable);
        container.setPadding(new Insets(10, 10, 10, 10));

        // Atribuie containerul la o scenă și afișează scena în primaryStage
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

    private int getIdFromName(String tableName, String itemName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM " + tableName + " WHERE name = ?")) {
            preparedStatement.setString(1, itemName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<String> loadItemsFromDatabase(String tableName, String columnName) {
        List<String> items = new ArrayList<>();
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + columnName + " FROM " + tableName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString(columnName);
                items.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
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
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT p.id, p.date, d.name as disease_name, m.name as medication_name " +
                             "FROM prescriptions p " +
                             "JOIN diseases d ON p.disease_id = d.id " +
                             "JOIN medications m ON p.medication_id = m.id")) {

            ResultSet resultSet = statement.executeQuery();
            ObservableList<Prescription> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String diseaseName = resultSet.getString("disease_name");
                String medicationName = resultSet.getString("medication_name");

                System.out.println("ID: " + id);
                System.out.println("Date: " + date);
                System.out.println("Disease Name: " + diseaseName);
                System.out.println("Medication Name: " + medicationName);

                data.addAll(new Prescription(id, date, diseaseName, medicationName));
            }

            prescriptionTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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