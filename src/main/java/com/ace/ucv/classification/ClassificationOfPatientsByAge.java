package com.ace.ucv.classification;

import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ClassificationOfPatientsByAge {
    private final ObservableList<Patient> patients;
    private final IPatientService patientService;

    public ClassificationOfPatientsByAge(ObservableList<Patient> patients) {
        this.patients = patients;
        this.patientService = new PatientService();
    }

   public Node getContent() {
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

       return layout;
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
