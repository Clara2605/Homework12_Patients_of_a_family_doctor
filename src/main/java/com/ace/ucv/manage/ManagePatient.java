package com.ace.ucv.manage;

import com.ace.ucv.classification.PatientSearchByFieldOfWorkDisplay;
import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Objects;

public class ManagePatient {

    private static final Logger logger = LogManager.getLogger(ManagePatient.class);

    private final ObservableList<Patient> patients;
    private TextField nameField;
    private TextField ageField;
    private TextField fieldOfWorkField;
    private TableView<Patient> patientTableView;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    private final IPatientService patientService;

    public ManagePatient(ObservableList<Patient> patients) {
        this.patients = patients;
        this.patientService = new PatientService();
    }

    public Node getContent() {
        VBox topVBox = new VBox();
        topVBox.setSpacing(10);
        topVBox.setPadding(new Insets(10));

        Button showPatientsButton = new Button("Show Patients by Field of Work");
        showPatientsButton.setPadding(new Insets(10));
        topVBox.getChildren().add(showPatientsButton);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

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

        deleteButton.setOnAction(this::handleDeletion);
        addButton.setOnAction(this::handleAddition);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));
        ageField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));
        fieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));

        patientTableView = createPatientTable(patients);

        GridPane.setConstraints(topVBox, 0, 0);
        GridPane.setColumnSpan(topVBox, 3);

        GridPane.setConstraints(patientTableView, 0, 4);
        GridPane.setColumnSpan(patientTableView, 3);

        grid.getChildren().addAll(
                nameLabel, nameField,
                ageLabel, ageField,
                fieldOfWorkLabel, fieldOfWorkField,
                addButton, editButton, deleteButton,
                patientTableView
        );

        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            logger.warn(String.format("Failed to create table of patient due to: %s", e.getMessage()));
        }

        patients.setAll(patientService.loadPatientsFromDatabase());
        showPatientsButton.setOnAction(e -> new PatientSearchByFieldOfWorkDisplay(patients).display());

        VBox layout = new VBox(topVBox, grid);
        layout.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        grid.getStyleClass().add("grid-pane");
        return layout;
    }

    private TableView<Patient> createPatientTable(ObservableList<Patient> patients) {
        TableView<Patient> patientTableView = new TableView<>();
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        final TableColumn<Patient, Void> actionsColumn = getTableColumn(patients, patientTableView);

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

    private TableColumn<Patient, Void> getTableColumn(ObservableList<Patient> patients, TableView<Patient> patientTableView) {
        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Actions");
        getCellFactory(patients, patientTableView, actionsColumn);
        return actionsColumn;
    }

    private void getCellFactory(ObservableList<Patient> patients, TableView<Patient> patientTableView, TableColumn<Patient, Void> actionsColumn) {
        actionsColumn.setCellFactory(param -> new TableCell<Patient, Void>() {
            private void handleDelete(ActionEvent e) {
                Patient selectedPatient = getTableView().getItems().get(getIndex());
                patientService.deletePatient(selectedPatient);
                patients.remove(selectedPatient);
            }

            private void handleEdit(ActionEvent e) {
                Patient selectedPatient = getTableView().getItems().get(getIndex());
                new EditPatientDialog(patientService, patientTableView).showEditPatientDialog(selectedPatient);
            }

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setTranslateX(10);

                editButton.setOnAction(this::handleEdit);
                deleteButton.setOnAction(this::handleDelete);
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

    private void handleDeletion(ActionEvent e) {
        Patient selectedPatient = patientTableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            patientService.deletePatient(selectedPatient);
            patients.remove(selectedPatient);
        }
    }

    private void handleAddition(ActionEvent e) {
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
    }
}