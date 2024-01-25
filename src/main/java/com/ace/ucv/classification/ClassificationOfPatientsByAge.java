package com.ace.ucv.classification;

import com.ace.ucv.model.Patient;
import com.ace.ucv.services.PatientService;
import com.ace.ucv.services.interfaces.IPatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ClassificationOfPatientsByAge {
    public static final int MAX_AGE_YOUNG = 30;
    public static final int MAX_AGE_MIDDLE = 60;
    public static final int SPACING = 10;
    public static final int RIGHT_LEFT_SPACING = 25;
    public static final String YOUNG_PATIENTS = "Young Patients";
    public static final String MIDDLE_AGED_PATIENTS = "Middle-Aged Patients";
    public static final String ELDERLY_PATIENTS = "Elderly Patients";
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

        ObservableList<Patient> youngPatients = filterPatientsByAge(patients, 0, MAX_AGE_YOUNG);
        ObservableList<Patient> middleAgedPatients = filterPatientsByAge(patients, MAX_AGE_YOUNG, MAX_AGE_MIDDLE);
        ObservableList<Patient> elderlyPatients = filterPatientsByAge(patients, MAX_AGE_MIDDLE, Integer.MAX_VALUE);

        // Table for young patients
        TitledPane youngPatientsPane = createPatientTable(YOUNG_PATIENTS, youngPatients);

        // Table for middle-aged patients
        TitledPane middleAgedPatientsPane = createPatientTable(MIDDLE_AGED_PATIENTS, middleAgedPatients);

        // Table for elderly patients
        TitledPane elderlyPatientsPane = createPatientTable(ELDERLY_PATIENTS, elderlyPatients);

        VBox layout = new VBox(SPACING);
        layout.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        layout.setPadding(new Insets(SPACING, RIGHT_LEFT_SPACING, SPACING, RIGHT_LEFT_SPACING));
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

        setupColumnWidths(tableView, nameColumn, ageColumn, fieldOfWorkColumn);

        TitledPane titledPane = new TitledPane(title, tableView);
        titledPane.setCollapsible(false);

        return titledPane;
    }

    /**
     * Sets the table column widths proportionally.
     * @param tableView The table to set the column widths for.
     * @param columns The columns of the table.
     */
    private void setupColumnWidths(TableView<Patient> tableView, TableColumn<Patient, ?>... columns) {
        double width = 1.0 / columns.length; // Calculate the width percentage for each column
        for (TableColumn<Patient, ?> column : columns) {
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(width));
        }
    }
}
