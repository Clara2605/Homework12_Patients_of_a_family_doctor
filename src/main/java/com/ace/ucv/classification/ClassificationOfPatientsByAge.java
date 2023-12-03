package com.ace.ucv.classification;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class ClassificationOfPatientsByAge {
    private static final Logger logger = LogManager.getLogger(ClassificationOfPatientsByAge.class);
    private Stage primaryStage;
    private ObservableList<Patient> patients;
    private IPatientService patientService;

    public ClassificationOfPatientsByAge(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.patientService = new PatientService();
    }

    public void start() {
        primaryStage.setTitle("Classification of Patients by Age");
        patients.setAll(patientService.loadPatientsFromDatabase());

        ObservableList<Patient> youngPatients = filterPatientsByAge(patients, 0, 30);
        ObservableList<Patient> middleAgedPatients = filterPatientsByAge(patients, 30, 60);
        ObservableList<Patient> elderlyPatients = filterPatientsByAge(patients, 60, Integer.MAX_VALUE);

        // Table for young patients
        TitledPane youngPatientsPane = createPatientTable("Young Patients", youngPatients);

        // Table for middle-aged patients
        TitledPane middleAgedPatientsPane = createPatientTable("Middle-Aged Patients", middleAgedPatients);

        // Table for elderly patients
        TitledPane elderlyPatientsPane = createPatientTable("Elderly Patients", elderlyPatients);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(youngPatientsPane, middleAgedPatientsPane, elderlyPatientsPane);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private ObservableList<Patient> filterPatientsByAge(ObservableList<Patient> patients, int minAge, int maxAge) {
        return FXCollections.observableArrayList(patients.stream()
                .filter(patient -> patient.getAge() >= minAge && patient.getAge() < maxAge)
                .collect(Collectors.toList()));
    }

    private TitledPane createPatientTable(String title, ObservableList<Patient> patients) {
        TableView<Patient> tableView = new TableView<>();
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        tableView.getColumns().addAll(nameColumn, ageColumn, fieldOfWorkColumn);
        tableView.setItems(patients);

        TitledPane titledPane = new TitledPane(title, tableView);
        titledPane.setCollapsible(false);

        return titledPane;
    }
}
