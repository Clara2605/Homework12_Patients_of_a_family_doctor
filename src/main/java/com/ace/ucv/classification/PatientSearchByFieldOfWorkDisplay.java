package com.ace.ucv.classification;

import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PatientSearchByFieldOfWorkDisplay {
    private ObservableList<Patient> allPatients;

    public PatientSearchByFieldOfWorkDisplay(ObservableList<Patient> allPatients) {
        this.allPatients = allPatients;
    }

    public void display() {
        Stage stage = new Stage();
        stage.setTitle("Patients by Field of Work");

        // Setup layout grid with controls for filtering
        GridPane grid = new GridPane();
        grid.setPadding(new javafx.geometry.Insets(10, 0, 20, 0));
        grid.setVgap(10);
        grid.setHgap(10);

        Label fieldOfWorkLabel = new Label("Field of Work:");
        grid.add(fieldOfWorkLabel, 0, 0);

        TextField fieldOfWorkFilter = new TextField();
        grid.add(fieldOfWorkFilter, 1, 0);

        Button filterButton = new Button("Filter");
        grid.add(filterButton, 2, 0);

        // Setup table view for displaying filtered patients
        TableView<Patient> filteredPatientTableView = createFilteredPatientTableView();

        // Action to filter patients based on field of work
        filterButton.setOnAction(e -> filterPatients(fieldOfWorkFilter.getText(), filteredPatientTableView));

        VBox vbox = new VBox(grid, filteredPatientTableView);
        Scene scene = new Scene(vbox, 720, 700);
        vbox.setPadding(new Insets(10, 25, 10, 25));
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and configures a TableView for displaying patient data.
     * Defines columns for patient's name, age, and field of work.
     *
     * @return A TableView object configured with the appropriate columns.
     */
    private TableView<Patient> createFilteredPatientTableView() {
        TableView<Patient> tableView = new TableView<>();

        // Column for patient's name
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Column for patient's age
        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        // Column for patient's field of work
        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        tableView.getColumns().addAll(nameColumn, ageColumn, fieldOfWorkColumn);
        setupColumnWidths(tableView, nameColumn, ageColumn, fieldOfWorkColumn);
        return tableView;
    }

    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }

    /**
     * Filters the patients based on the specified field of work.
     * Updates the provided TableView with patients that match the field of work criterion.
     *
     * @param fieldOfWork The field of work to filter by.
     * @param tableView The TableView to update with the filtered patients.
     */
    private void filterPatients(String fieldOfWork, TableView<Patient> tableView) {
        ObservableList<Patient> filteredPatients = FXCollections.observableArrayList();
        for (Patient patient : allPatients) {
            if (patient.getFieldOfWork().equalsIgnoreCase(fieldOfWork)) {
                filteredPatients.add(patient);
            }
        }
        tableView.setItems(filteredPatients);
    }
}
