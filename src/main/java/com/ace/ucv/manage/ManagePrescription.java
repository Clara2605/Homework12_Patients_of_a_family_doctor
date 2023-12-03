package com.ace.ucv.manage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.manage.AddPrescriptionDialog;
import com.ace.ucv.services.PrescriptionService;
import com.ace.ucv.services.interfaces.IPrescriptionService;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ManagePrescription {
    private static final Logger logger = LogManager.getLogger(ManagePrescription.class);
    private Stage primaryStage;
    private ObservableList<Patient> patients;
    private List<String> diseases;
    private List<String> medications;
    private ComboBox<Patient> patientComboBox;
    private TextField ageTextField;
    private TableView<Prescription> prescriptionTable;
    private IPrescriptionService prescriptionService;
    private ObservableList<Prescription> prescriptions;
    private Button editButton;
    private Button deleteButton;

    public ManagePrescription(Stage primaryStage, ObservableList<Patient> patients) {
        this.prescriptionService = new PrescriptionService();
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = prescriptionService.loadItemsFromDatabase("diseases", "name");
        this.medications = prescriptionService.loadItemsFromDatabase("medications", "name");
        this.prescriptionTable = new TableView<>();

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        setupButtons();
        prescriptions = FXCollections.observableArrayList();
        prescriptions.addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }

    private void setupButtons() {
        editButton.setOnAction(e -> editSelectedPrescription());
        deleteButton.setOnAction(e -> deleteSelectedPrescription());

        // Disabling buttons initially
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable buttons only when a row is selected
        prescriptionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isItemSelected = newSelection != null;
            editButton.setDisable(!isItemSelected);
            deleteButton.setDisable(!isItemSelected);
        });
    }

    private void editSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            showEditPrescriptionDialog(selectedPrescription);
        }
    }

    private void deleteSelectedPrescription() {
        Prescription selectedPrescription = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selectedPrescription != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this prescription?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (prescriptionService.deletePrescription(selectedPrescription.getId())) {
                        prescriptionTable.getItems().remove(selectedPrescription);
                    }
                }
            });
        }
    }


    public void start() {
        loadPatientsFromDatabase();
        setupPrescriptionTable(); // Add this line
        AddPrescriptionDialog addPrescriptionDialog = new AddPrescriptionDialog(patients, diseases, medications, prescriptionService, prescriptionTable);
        addPrescriptionDialog.show();
        loadPrescriptionsFromDatabase();
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

        TableColumn<Prescription, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Prescription, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                // Set up buttons (actions, styles)
                setupEditButton(editButton);
                setupDeleteButton(deleteButton);
            }

            private void setupEditButton(Button button) {
                button.setOnAction(e -> {
                    Prescription selectedPrescription = (Prescription) getTableView().getItems().get(getIndex());
                    showEditPrescriptionDialog(selectedPrescription);
                });
            }

            private void setupDeleteButton(Button button) {
                button.setOnAction(e -> {
                    Prescription selectedPrescription = (Prescription) getTableView().getItems().get(getIndex());
                    prescriptionService.deletePrescription(selectedPrescription.getId());
                    prescriptions.remove(selectedPrescription);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        prescriptionTable.getColumns().addAll(idColumn, dateColumn, diseaseColumn, medicationColumn, actionsColumn);

        VBox container = new VBox(prescriptionTable);
        container.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(container, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
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
            logger.error(String.format("Error loading patients from the database %s", e.getMessage()));
        }
    }

    private void loadPrescriptionsFromDatabase() {
        ObservableList<Prescription> prescriptions = prescriptionService.loadPrescriptionsFromDatabase();
        prescriptionTable.setItems(prescriptions);
    }

    private void showEditPrescriptionDialog(Prescription prescription) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Prescription");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

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

    private void refreshTable() {
        prescriptionTable.getItems().clear();
        prescriptionTable.getItems().addAll(prescriptionService.loadPrescriptionsFromDatabase());
    }
}