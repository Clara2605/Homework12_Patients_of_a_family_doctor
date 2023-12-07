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

    /**
     * Constructor that initializes the class with a list of patients.
     * It also creates a patient service instance for further operations.
     *
     * @param patients List of patients to be classified.
     */
    public ClassificationOfPatientsByAge(ObservableList<Patient> patients) {
        this.patients = patients;
        this.patientService = new PatientService();
    }

    /**
     * Generates a Node containing the categorized patient data.
     * It classifies patients into three age groups and displays them in separate tables.
     *
     * @return Node containing the layout with patient classification.
     */
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

    /**
     * Filters patients by their age.
     * It returns a list of patients whose age falls within the specified range.
     *
     * @param patients ObservableList of Patient objects.
     * @param minAge   The minimum age for the filter.
     * @param maxAge   The maximum age for the filter.
     * @return ObservableList of filtered patients.
     */
    private ObservableList<Patient> filterPatientsByAge(ObservableList<Patient> patients, int minAge, int maxAge) {
        return FXCollections.observableArrayList(patients.stream()
                .filter(patient -> patient.getAge() >= minAge && patient.getAge() < maxAge)
                .collect(Collectors.toList()));
    }

    /**
     * Creates a TitledPane containing a table for displaying patient information.
     * It sets up columns for patient name, age, and field of work.
     *
     * @param title    Title for the TitledPane.
     * @param patients ObservableList of patients to be displayed in the table.
     * @return TitledPane with a table of patients.
     */
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
