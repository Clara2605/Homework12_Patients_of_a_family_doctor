package com.ace.ucv;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private TextField nameField, ageField, fieldOfWorkField;
    private ListView<Patient> patientListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Medic Application");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
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

        Button addButton = new Button("Add Patient");
        GridPane.setConstraints(addButton, 1, 3);
        addButton.setOnAction(e -> addPatient());

        Label patientListLabel = new Label("Patient List:");
        GridPane.setConstraints(patientListLabel, 0, 4);
        patientListView = new ListView<>();
        GridPane.setConstraints(patientListView, 1, 4);
        patientListView.setItems(patients);

        patientListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showPatientDetails(newValue);
            }
        });

        GridPane.setColumnSpan(patientListView, 2);

        grid.getChildren().addAll(
                nameLabel, nameField,
                ageLabel, ageField,
                fieldOfWorkLabel, fieldOfWorkField,
                addButton,
                patientListLabel, patientListView
        );

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addPatient() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String fieldOfWork = fieldOfWorkField.getText();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO patients (name, age, field_of_work) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, fieldOfWork);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Patient patient = new Patient(name, age, fieldOfWork);
        patients.add(patient);

        nameField.clear();
        ageField.clear();
        fieldOfWorkField.clear();
    }

    private void showPatientDetails(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Patient Details");
        alert.setHeaderText(null);
        alert.setContentText("Name: " + patient.getName() + "\nAge: " + patient.getAge() + "\nField of Work: " + patient.getFieldOfWork());
        alert.showAndWait();
    }
}
