package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import java.time.format.DateTimeFormatter;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

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
    private TableView<Prescription> prescriptionTable;

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = loadDiseasesFromDatabase();
        this.medications = loadMedicationsFromDatabase();
        this.prescriptionTable = new TableView<>();
    }

    public void start() {
        loadPatientsFromDatabase();
        setupPrescriptionTable();
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
                String diseaseName = diseaseComboBox.getValue();
                String medicationName = medicationComboBox.getValue();

                if (date != null && selectedPatient != null && diseaseName != null && medicationName != null) {
                    // Here, get the disease and medication IDs based on their names
                    int diseaseId = getDiseaseId(diseaseName);
                    int medicationId = getMedicationId(medicationName);

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
        prescriptionTable = new TableView<>();
        TableColumn<Prescription, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Prescription, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Prescription, String> diseaseColumn = new TableColumn<>("Disease");
        diseaseColumn.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));

        TableColumn<Prescription, String> medicationColumn = new TableColumn<>("Medication");
        medicationColumn.setCellValueFactory(new PropertyValueFactory<>("medicationName"));

        prescriptionTable.getColumns().addAll(idColumn, dateColumn, diseaseColumn, medicationColumn);

        // Crează un container (de exemplu, VBox) pentru a adăuga TableView și alte elemente, dacă este necesar
        VBox container = new VBox(prescriptionTable);
        container.setPadding(new Insets(10));

        // Atribuie containerul la o scenă și afișează scena în primaryStage
        Scene scene = new Scene(container, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void savePrescription(Patient patient, LocalDate date, String diseaseId, String medicationId) {
        int patientId = patient.getId();

        if (Integer.valueOf(diseaseId) != -1 && Integer.valueOf(medicationId) != -1) {
            try (Connection connection = DatabaseManager.connect()) {
                connection.setAutoCommit(false); // Disable auto-commit


                // Insert prescription data into the prescriptions table
                String insertPrescriptionSQL = "INSERT INTO prescriptions (date, patient_id, disease_id, medication_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPrescriptionSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, String.valueOf(date));
                    preparedStatement.setInt(2, patientId);
                    preparedStatement.setString(3, diseaseId);
                    preparedStatement.setString(4, medicationId);
                    preparedStatement.executeUpdate();

                    // Commit the transaction
                    connection.commit();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int prescriptionId = generatedKeys.getInt(1);
                        Prescription prescription = new Prescription(prescriptionId, date, diseaseId, medicationId);
                        prescriptionTable.getItems().add(prescription);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Rollback the transaction in case of an error
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

    private int getDiseaseId(String diseaseName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM diseases WHERE name = ?")) {
            preparedStatement.setString(1, diseaseName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the disease was not found
    }

    private int getMedicationId(String medicationName) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM medications WHERE name = ?")) {
            preparedStatement.setString(1, medicationName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the medication was not found
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
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String diseaseName = resultSet.getString("disease_name");
                String medicationName = resultSet.getString("medication_name");

                data.add(new Prescription(id, date, diseaseName, medicationName));
            }

            prescriptionTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String getDiseaseName(String diseaseId) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM diseases WHERE id = ?")) {
            preparedStatement.setInt(1, Integer.parseInt(diseaseId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Returnează o valoare implicită sau semnalează eroarea cum doriți
    }

    private String getMedicationName(String medicationId) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM medications WHERE id = ?")) {
            preparedStatement.setInt(1, Integer.parseInt(medicationId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Returnează o valoare implicită sau semnalează eroarea cum doriți
    }

}
