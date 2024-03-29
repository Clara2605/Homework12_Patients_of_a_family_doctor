package com.ace.ucv.manage;

import com.ace.ucv.classification.PatientSearchByFieldOfWorkDisplay;
import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Objects;

public class ManagePatient {

    private static final Logger logger = LogManager.getLogger(ManagePatient.class);
    public static final String A_Z_A_Z = "[a-zA-Z ]+";

    private final ObservableList<Patient> patients;
    private TextField nameField;
    private TextField ageField;
    private TextField fieldOfWorkField;
    private TableView<Patient> patientTableView;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    private final IPatientService patientService;


    /**
     * Constructor for ManagePatient.
     * Initializes the class with a list of patients and sets up the patient service.
     *
     * @param patients ObservableList of Patients.
     */
    public ManagePatient(ObservableList<Patient> patients) {
        this.patients = patients;
        this.patientService = new PatientService();
    }

    /**
     * Creates and configures a VBox for the top section of the layout.
     *
     * @return Configured VBox.
     */
    private VBox createTopVBox() {
        VBox topVBox = new VBox(10); // spacing
        topVBox.setPadding(new Insets(10, 0, 0, 0));

        Button showPatientsButton = new Button("Show Patients by Field of Work");
        showPatientsButton.setPadding(new Insets(10));
        showPatientsButton.setOnAction(e -> new PatientSearchByFieldOfWorkDisplay(patients).display());

        topVBox.getChildren().add(showPatientsButton);
        return topVBox;
    }

    /**
     * Creates and configures a GridPane for the form layout.
     *
     * @return Configured GridPane.
     */
    private GridPane createAndConfigureGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 0, 10, 0));
        grid.setVgap(10);
        grid.setHgap(10);
        return grid;
    }

    public Node getContent() {
        VBox topVBox = createTopVBox();
        GridPane grid = createAndConfigureGridPane();

        Label nameLabel = getName();
        Label ageLabel = getAgeLabel();
        Label fieldOfWorkLabel = getFieldOfWorkLabel();

        createAndConfigureButtons();
        establishTopVBoxDimension(topVBox);
        patientTableView = createPatientTable(patients);
        GridPane.setConstraints(patientTableView, 0, 4);
        GridPane.setColumnSpan(patientTableView, 3);

        grid.getChildren().addAll(
                nameLabel, nameField,
                ageLabel, ageField,
                fieldOfWorkLabel, fieldOfWorkField,
                addButton, editButton, deleteButton,
                patientTableView
        );

        initializeDatabase();
        patients.setAll(patientService.loadPatientsFromDatabase());

        return getvBox(topVBox, grid);
    }

    private VBox getvBox(VBox topVBox, GridPane grid) {
        VBox layout = new VBox(topVBox, grid, patientTableView);
        layout.setPadding(new Insets(10, 25, 10, 25));
        layout.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        VBox.setVgrow(patientTableView, Priority.ALWAYS);
        Region space = new Region();
        space.setPrefHeight(20); // Setarea înălțimii preferate pentru spațiu
        layout.getChildren().add(space);
        return layout;
    }

    private static void establishTopVBoxDimension(VBox topVBox) {
        GridPane.setConstraints(topVBox, 0, 0);
        GridPane.setColumnSpan(topVBox, 3);
    }

    private Label getFieldOfWorkLabel() {
        Label fieldOfWorkLabel = new Label("Field of Work:");
        GridPane.setConstraints(fieldOfWorkLabel, 0, 2);
        fieldOfWorkField = new TextField();
        GridPane.setConstraints(fieldOfWorkField, 1, 2);
        return fieldOfWorkLabel;
    }

    private Label getAgeLabel() {
        Label ageLabel = new Label("Age:");
        GridPane.setConstraints(ageLabel, 0, 1);
        ageField = new TextField();
        GridPane.setConstraints(ageField, 1, 1);
        return ageLabel;
    }

    private Label getName() {
        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 0);
        return nameLabel;
    }

    /**
     * Creates and sets up buttons for managing patients.
     */
    private void createAndConfigureButtons() {
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
        attachListeners();
    }

    /**
     * Attaches listeners to form fields to enable or disable the add button based on input.
     */
    private void attachListeners() {
        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));
        ageField.textProperty().addListener((observable, oldValue, newValue) ->
                updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));
        fieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) ->
                updateAddButtonState(nameField, ageField, fieldOfWorkField, addButton));
    }

    /**
     * Initializes the database and loads patients from it.
     */
    private void initializeDatabase() {
        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            logger.error("Failed to initialize the database: " + e.getMessage(), e);
        }
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

        setupColumnWidths(patientTableView, nameColumn, ageColumn, fieldOfWorkColumn, actionsColumn);

        return patientTableView;
    }

    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }

    private TableColumn<Patient, Void> getTableColumn(ObservableList<Patient> patients, TableView<Patient> patientTableView) {
        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Actions");
        setupActionsColumn(patients, patientTableView, actionsColumn);
        return actionsColumn;
    }

    private void setupActionsColumn(ObservableList<Patient> patients, TableView<Patient> patientTableView, TableColumn<Patient, Void> actionsColumn) {
        actionsColumn.setCellFactory(param -> new ActionCell(patients, patientTableView));
    }

    private class ActionCell extends TableCell<Patient, Void> {
        private final Button editButton;
        private final Button deleteButton;

        public ActionCell(ObservableList<Patient> patients, TableView<Patient> patientTableView) {
            editButton = createButton("Edit", "edit-button", e -> handleEdit(patientTableView));
            deleteButton = createButton("Delete", "delete-button", e -> handleDelete(patients));

            HBox buttons = new HBox(editButton, deleteButton);
            buttons.setSpacing(10); // Adjust the spacing if needed
            setGraphic(buttons);
        }

        private Button createButton(String text, String styleClass, EventHandler<ActionEvent> handler) {
            Button button = new Button(text);
            button.getStyleClass().add(styleClass);
            button.setOnAction(handler);
            return button;
        }

        private void handleDelete(ObservableList<Patient> patients) {
            Patient selectedPatient = getTableView().getItems().get(getIndex());
            patientService.deletePatient(selectedPatient);
            patients.remove(selectedPatient);
        }

        private void handleEdit(TableView<Patient> patientTableView) {
            Patient selectedPatient = getTableView().getItems().get(getIndex());
            new EditPatientDialog(patientService, patientTableView).showEditPatientDialog(selectedPatient);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(editButton, deleteButton);
                buttons.setSpacing(10);
                buttons.setAlignment(javafx.geometry.Pos.CENTER);
                setGraphic(buttons);
            }
        }
    }

    /**
     * Validates input fields for adding or editing a patient.
     *
     * @return True if the fields are valid, False otherwise.
     */
    private boolean validateFields() {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();
        String fieldOfWork = fieldOfWorkField.getText().trim();

        boolean validName = !name.isEmpty() && name.matches(A_Z_A_Z);
        boolean validAge = !age.isEmpty() && age.matches("\\d+");
        boolean validFieldOfWork = !fieldOfWork.isEmpty() && fieldOfWork.matches(A_Z_A_Z);

        return validName && validAge && validFieldOfWork;
    }

    /**
     * Updates the state of the add button based on the input in the form fields.
     *
     * @param nameField TextField for the patient's name.
     * @param ageField TextField for the patient's age.
     * @param fieldOfWorkField TextField for the patient's field of work.
     * @param addButton The button to be enabled or disabled.
     */
    private void updateAddButtonState(TextField nameField, TextField ageField, TextField fieldOfWorkField, Button addButton) {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();
        String fieldOfWork = fieldOfWorkField.getText().trim();

        boolean isValid = !name.isEmpty() && name.matches(A_Z_A_Z)
                && !age.isEmpty() && age.matches("\\d+")
                && !fieldOfWork.isEmpty() && fieldOfWork.matches(A_Z_A_Z);

        addButton.setDisable(!isValid);
    }

    /**
     * Handles the deletion of a selected patient.
     *
     * @param e The ActionEvent triggered by the delete button.
     */
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