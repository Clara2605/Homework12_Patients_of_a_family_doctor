package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private TextField nameField, ageField, fieldOfWorkField;
    private TableView<Patient> patientTableView;
    private Button addButton;

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
        ImageView nameCheck = new ImageView(); // Crearea ImageView pentru Name
        nameCheck.setFitWidth(20);
        nameCheck.setFitHeight(20);
        GridPane.setConstraints(nameCheck, 2, 0);

        Label ageLabel = new Label("Age:");
        GridPane.setConstraints(ageLabel, 0, 1);
        ageField = new TextField();
        GridPane.setConstraints(ageField, 1, 1);
        ImageView ageCheck = new ImageView(); // Crearea ImageView pentru Age
        ageCheck.setFitWidth(20);
        ageCheck.setFitHeight(20);
        GridPane.setConstraints(ageCheck, 2, 1);

        Label fieldOfWorkLabel = new Label("Field of Work:");
        GridPane.setConstraints(fieldOfWorkLabel, 0, 2);
        fieldOfWorkField = new TextField();
        GridPane.setConstraints(fieldOfWorkField, 1, 2);
        ImageView fieldOfWorkCheck = new ImageView(); // Crearea ImageView pentru Field of Work
        fieldOfWorkCheck.setFitWidth(20);
        fieldOfWorkCheck.setFitHeight(20);
        GridPane.setConstraints(fieldOfWorkCheck, 2, 2);

        addButton = new Button("Add Patient");
        GridPane.setConstraints(addButton, 0, 3);
        GridPane.setColumnSpan(addButton, 2); // Lărgirea butonului pe două coloane
        addButton.setDisable(true); // Dezactivează butonul inițial

        addButton.setOnAction(e -> {
            if (validateFields()) {
                addPatient();
            }
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameCheck, newValue); // Actualizează starea butonului și imaginea pentru Name
        });

        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(ageCheck, newValue); // Actualizează starea butonului și imaginea pentru Age
        });

        fieldOfWorkField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(fieldOfWorkCheck, newValue); // Actualizează starea butonului și imaginea pentru Field of Work
        });

        patientTableView = new TableView<>();
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> fieldOfWorkColumn = new TableColumn<>("Field of Work");
        fieldOfWorkColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOfWork"));

        patientTableView.getColumns().addAll(nameColumn, ageColumn, fieldOfWorkColumn);
        patientTableView.setItems(patients);
        GridPane.setConstraints(patientTableView, 0, 4);
        GridPane.setConstraints(patientTableView, 1, 4); // Lărgirea tabelului pe două coloane

        grid.getChildren().addAll(
                nameLabel, nameField, nameCheck,
                ageLabel, ageField, ageCheck,
                fieldOfWorkLabel, fieldOfWorkField, fieldOfWorkCheck,
                addButton,
                patientTableView
        );

        Scene scene = new Scene(grid, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Crează tabela la începutul aplicației
        CreateTable.createTable();
        loadPatients();
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

    private void updateAddButtonState(ImageView imageView, String newValue) {
        boolean isValid = !newValue.isEmpty() && newValue.matches("[a-zA-Z ]+");
        if (isValid) {
            imageView.setImage(new Image("/green-checkbox.jpg")); // Încarcă imaginea corectă
        } else {
            imageView.setImage(null); // Dacă textul nu este valid, nu afișăm nimic
        }
        if (ageField.getText().isEmpty() || !ageField.getText().matches("\\d+")) {
            imageView.setImage(null); // Șterge imaginea dacă vârsta nu este un număr valid
        } else {
            int age = Integer.parseInt(ageField.getText());
            if (age < 0 || age > 100) {
                imageView.setImage(null); // Șterge imaginea dacă vârsta nu se află în intervalul specificat
            }
        }
        updateAddButtonState();
    }


    private void updateAddButtonState() {
        addButton.setDisable(!validateFields());
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

    private void loadPatients() {
        patients.clear();
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
    }
}