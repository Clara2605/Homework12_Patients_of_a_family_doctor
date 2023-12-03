package com.ace.ucv.classification;

import com.ace.ucv.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        GridPane grid = new GridPane();
        grid.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label fieldOfWorkLabel = new Label("Field of Work:");
        grid.add(fieldOfWorkLabel, 0, 0);

        TextField fieldOfWorkFilter = new TextField();
        grid.add(fieldOfWorkFilter, 1, 0);

        Button filterButton = new Button("Filter");
        grid.add(filterButton, 2, 0);

        TableView<Patient> filteredPatientTableView = createFilteredPatientTableView();
        filterButton.setOnAction(e -> filterPatients(fieldOfWorkFilter.getText(), filteredPatientTableView));

        VBox vbox = new VBox(grid, filteredPatientTableView);
        Scene scene = new Scene(vbox, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private TableView<Patient> createFilteredPatientTableView() {
        TableView<Patient> tableView = new TableView<>();
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        tableView.getColumns().addAll(nameColumn, ageColumn, fieldOfWorkColumn);
        return tableView;
    }

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
