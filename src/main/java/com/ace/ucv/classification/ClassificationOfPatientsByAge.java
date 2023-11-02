package com.ace.ucv.classification;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
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

    public ClassificationOfPatientsByAge(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
    }

    public void start() {
        primaryStage.setTitle("Classification of Patients by Age");

        // Connect to database and retrieve data
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM patients");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String fieldOfWork = resultSet.getString("field_of_work");
                Patient patient = new Patient(name, age, fieldOfWork);
                patients.add(patient);
            }
        } catch (SQLException e) {
            logger.error(String.format("Error retrieving patients from database due to: %s", e.getMessage()));
            throw new RuntimeException(String.format("Database error occurred: %s", e.getMessage()));
        }

        // Filter and classify patients by age
        ObservableList<Patient> youngPatients = FXCollections.observableArrayList(patients.stream()
                .filter(patient -> patient.getAge() < 30)
                .collect(Collectors.toList()));
        ObservableList<Patient> middleAgedPatients = FXCollections.observableArrayList(patients.stream()
                .filter(patient -> patient.getAge() >= 30 && patient.getAge() < 60)
                .collect(Collectors.toList()));
        ObservableList<Patient> elderlyPatients = FXCollections.observableArrayList(patients.stream()
                .filter(patient -> patient.getAge() >= 60)
                .collect(Collectors.toList()));

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
