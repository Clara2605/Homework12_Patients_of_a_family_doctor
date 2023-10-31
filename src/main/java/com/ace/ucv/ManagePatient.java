package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagePatient {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private TextField nameField, ageField, fieldOfWorkField;
    private TableView<Patient> patientTableView;
    private Button addButton, editButton, deleteButton;
    private Stage primaryStage;

    public ManagePatient(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
    }

    public void start() {
        primaryStage.setTitle("Medic Application");

        GridPane grid = new GridPane();
        grid.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 0);

        Label ageLabel = new Label("Age:");
        GridPane.setConstraints(ageLabel, 0, 1);
        ageField = new TextField();
        GridPane.setConstraints(ageField, 1, 1);

        Label fieldOfWorkLabel = new Label("Field of Work:");
        GridPane.setConstraints(fieldOfWorkLabel, 0, 2);
        fieldOfWorkField = new TextField();
        GridPane.setConstraints(fieldOfWorkField, 1, 2);

        addButton = new Button("Add Patient");
        GridPane.setConstraints(addButton, 0, 3);
        GridPane.setColumnSpan(addButton, 2);
        addButton.setDisable(true);

        editButton = new Button("Edit Patient");
        GridPane.setConstraints(editButton, 1, 3);
        editButton.setDisable(true);
        editButton.setVisible(false);

        editButton.setOnAction(e -> {
            Patient selectedPatient = patientTableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                showEditPatientDialog(primaryStage, selectedPatient);
            }
        });

        deleteButton = new Button("Delete Patient");
        GridPane.setConstraints(deleteButton, 2, 3);
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);

        deleteButton.setOnAction(e -> {
            Patient selectedPatient = patientTableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                selectedPatient.deletePatient();
                patients.remove(selectedPatient);
            }
        });

        addButton.setOnAction(e -> {
            if (validateFields()) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String fieldOfWork = fieldOfWorkField.getText();
                Patient.addPatient(name, age, fieldOfWork);

                Patient patient = new Patient(name, age, fieldOfWork);
                patients.add(patient);

                nameField.clear();
                ageField.clear();
                fieldOfWorkField.clear();
            }
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton);
        });

        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton);
        });

        fieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton);
        });

        patientTableView = new TableView<>();
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Patient, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(e -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());
                    showEditPatientDialog(primaryStage, selectedPatient);
                });

                deleteButton.setOnAction(e -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());
                    selectedPatient.deletePatient();
                    patients.remove(selectedPatient);
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

        patientTableView.getColumns().addAll(nameColumn, ageColumn, fieldOfWorkColumn, actionsColumn);
        patientTableView.setItems(patients);
        patientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        GridPane.setConstraints(patientTableView, 0, 4);
        GridPane.setColumnSpan(patientTableView, 3);

        grid.getChildren().addAll(
                nameLabel, nameField,
                ageLabel, ageField,
                fieldOfWorkLabel, fieldOfWorkField,
                addButton, editButton, deleteButton,
                patientTableView
        );

        Scene scene = new Scene(new VBox(grid), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        CreateTable.createTable();
        patients.setAll(Patient.loadPatientsFromDatabase());
    }

    private boolean validateFields() {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();
        String fieldOfWork = fieldOfWorkField.getText().trim();

        boolean validName = !name.isEmpty() && name.matches("[a-zA-Z ]+");
        boolean validAge = !age.isEmpty() && age.matches("\\d+");
        boolean validFieldOfWork = !fieldOfWork.isEmpty() && fieldOfWork.matches("[a-zA-Z ]+");

        return validName && validAge && validFieldOfWork;
    }

    private void updateAddButtonState(TextField nameField, TextField ageField, TextField fieldOfWorkField, Button addButton) {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();
        String fieldOfWork = fieldOfWorkField.getText().trim();

        boolean isValid = !name.isEmpty() && name.matches("[a-zA-Z ]+")
                && !age.isEmpty() && age.matches("\\d+")
                && !fieldOfWork.isEmpty() && fieldOfWork.matches("[a-zA-Z ]+");

        addButton.setDisable(!isValid);
    }

    private void showEditPatientDialog(Stage primaryStage, Patient patient) {
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle("Edit Patient");
        dialog.setHeaderText("Edit patient information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(patient.getName());
        TextField editAgeField = new TextField(String.valueOf(patient.getAge()));
        TextField editFieldOfWorkField = new TextField(patient.getFieldOfWork());

        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);
        editGrid.add(new Label("Age:"), 0, 1);
        editGrid.add(editAgeField, 1, 1);
        editGrid.add(new Label("Field of Work:"), 0, 2);
        editGrid.add(editFieldOfWorkField, 1, 2);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField);
        });

        editAgeField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField);
        });

        editFieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField, editAgeField, editFieldOfWorkField);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                int editedAge = Integer.parseInt(editAgeField.getText());
                String editedFieldOfWork = editFieldOfWorkField.getText();

                patient.editPatient(editedName, editedAge, editedFieldOfWork);
                patientTableView.refresh();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField, TextField editAgeField, TextField editFieldOfWorkField) {
        String editedName = editNameField.getText().trim();
        String editedAge = editAgeField.getText().trim();
        String editedFieldOfWork = editFieldOfWorkField.getText().trim();

        boolean isValid = !editedName.isEmpty() && editedName.matches("[a-zA-Z ]+")
                && !editedAge.isEmpty() && editedAge.matches("\\d+")
                && !editedFieldOfWork.isEmpty() && editedFieldOfWork.matches("[a-zA-Z ]+");

        saveButton.setDisable(!isValid);
    }
}
