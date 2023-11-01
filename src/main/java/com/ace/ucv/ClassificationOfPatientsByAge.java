package com.ace.ucv;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class ClassificationOfPatientsByAge {
    private Stage primaryStage;
    private ObservableList<Patient> patients;

    public ClassificationOfPatientsByAge(Stage primaryStage, ObservableList<Patient> patients) {
        this.primaryStage = primaryStage;
        this.patients = patients;
    }

    public void start() {
        primaryStage.setTitle("Classification of Patients by Age");

        // Conectare la baza de date
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
            e.printStackTrace();
        }

        // Filtrare și clasificare pacienți în funcție de vârstă
        ObservableList<Patient> youngPatients = FXCollections.observableArrayList();
        ObservableList<Patient> middleAgedPatients = FXCollections.observableArrayList();
        ObservableList<Patient> elderlyPatients = FXCollections.observableArrayList();

        youngPatients.setAll(patients.stream().filter(patient -> patient.getAge() < 30).collect(Collectors.toList()));
        middleAgedPatients.setAll(patients.stream().filter(patient -> patient.getAge() >= 30 && patient.getAge() < 60).collect(Collectors.toList()));
        elderlyPatients.setAll(patients.stream().filter(patient -> patient.getAge() >= 60).collect(Collectors.toList()));

        // Tabel pentru pacienții tineri
        TitledPane youngPatientsPane = createPatientTable("Young Patients", youngPatients);

        // Tabel pentru pacienții de vârsta mijlocie
        TitledPane middleAgedPatientsPane = createPatientTable("Middle-Aged Patients", middleAgedPatients);

        // Tabel pentru pacienții bătrâni
        TitledPane elderlyPatientsPane = createPatientTable("Elderly Patients", elderlyPatients);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(youngPatientsPane, middleAgedPatientsPane, elderlyPatientsPane);

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
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
