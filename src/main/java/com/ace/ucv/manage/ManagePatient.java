package com.ace.ucv.manage;

import com.ace.ucv.classification.PatientSearchByFieldOfWorkDisplay;
import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;

public class ManagePatient {
    private ObservableList<Patient> patients;
    private TextField nameField, ageField, fieldOfWorkField;
    private TableView<Patient> patientTableView;
    private Button addButton, editButton, deleteButton, showPatientsButton;
    private Stage primaryStage;
    private IPatientService patientService;

    public ManagePatient(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.patientService = new PatientService();
    }

    public void start() {
        primaryStage.setTitle("Manage Patients");

        VBox topVBox = new VBox();
        topVBox.setSpacing(10);
        topVBox.setPadding(new Insets(10));
        topVBox.alignmentProperty();

        showPatientsButton = new Button("Show Patients by Field of Work");
        showPatientsButton.setPadding(new javafx.geometry.Insets(10));
        topVBox.getChildren().add(showPatientsButton);


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
        addButton.setPadding(new Insets(10));

        editButton = new Button("Edit Patient");
        GridPane.setConstraints(editButton, 1, 3);
        editButton.setDisable(true);
        editButton.setVisible(false);
        editButton.getStyleClass().add("edit-button");

        deleteButton = new Button("Delete Patient");
        GridPane.setConstraints(deleteButton, 2, 3);
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);
        deleteButton.getStyleClass().add("delete-button");

        deleteButton.setOnAction(e -> {
            Patient selectedPatient = patientTableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {

                patientService.deletePatient(selectedPatient);
                patients.remove(selectedPatient);
            }
        });

        addButton.setOnAction(e -> {
            if (validateFields()) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String fieldOfWork = fieldOfWorkField.getText();
                Patient patient = new Patient(name, age, fieldOfWork);

                patientService.addPatient(patient);
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

        patientTableView = createPatientTable(patients);


        GridPane.setConstraints(topVBox, 0, 0);
        GridPane.setColumnSpan(topVBox, 3);

        GridPane.setConstraints(patientTableView, 0, 4);
        GridPane.setColumnSpan(patientTableView, 3);

        grid.getChildren().addAll(
//                showPatientsButton,
                nameLabel, nameField,
                ageLabel, ageField,
                fieldOfWorkLabel, fieldOfWorkField,
                addButton, editButton, deleteButton,
                patientTableView
        );

        Scene scene = new Scene(new VBox(topVBox, grid), 500, 500);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            e.printStackTrace();

        }

        patients.setAll(patientService.loadPatientsFromDatabase());

        showPatientsButton.setOnAction(e -> new PatientSearchByFieldOfWorkDisplay(patients).display());
    }

    private TableView<Patient> createPatientTable(ObservableList<Patient> patients) {
        TableView<Patient> patientTableView = new TableView<>();
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
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setTranslateX(10);

                editButton.setOnAction(e -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());
                    new EditPatientDialog(patientService, patientTableView).showEditPatientDialog(selectedPatient);
                });

                deleteButton.setOnAction(e -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());

                    patientService.deletePatient(selectedPatient);
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

        return patientTableView;
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

}