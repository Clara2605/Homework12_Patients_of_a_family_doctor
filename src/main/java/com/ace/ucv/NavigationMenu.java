package com.ace.ucv;

import com.ace.ucv.model.*;
import com.ace.ucv.services.interfaces.IDiseaseService;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class NavigationMenu extends MenuBar {

    private Stage primaryStage;
    private ObservableList<Patient> patients;
    private ObservableList<Disease> diseases;
    private ObservableList<Medication> medications;
    private ObservableList<Prescription> prescriptions;
    private IDiseaseService diseaseService;

    public NavigationMenu(Stage primaryStage, ObservableList<Patient> patients, ObservableList<Disease> diseases, ObservableList<Medication> medications, ObservableList<Prescription> prescriptions) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = diseases;
        this.medications = medications;
        this.prescriptions = prescriptions;
        initializeMenus();
    }

    private void initializeMenus() {
        this.getMenus().addAll(
                createManagePatientsMenu(),
                createManagePrescriptionMenu(),
                createManageDiseasesMenu(),
                createManageMedicationsMenu()
        );
    }

    private Menu createManagePatientsMenu() {
        Menu menu = new Menu("Manage Patients");
        menu.getItems().add(createMenuItem("Manage Patients", this::handleManagePatients));
        menu.getItems().add(createMenuItem("Classification of Patients by Age", this::handleClassifyPatients));
        return menu;
    }


    private Menu createManagePrescriptionMenu() {
        Menu menu = new Menu("Manage Prescription");
        menu.getItems().add(createMenuItem("Manage Prescription", this::handleManagePrescription));

        menu.getItems().add(createMenuItem("Search Diseases", this::handleDiseaseSearch));
        menu.getItems().add(createMenuItem("Search Medication", this::handleMedicationSearch));
        menu.getItems().add(createMenuItem("Search by Prescriptions per Month", this::handlePrescriptionSearch));
        menu.getItems().add(createMenuItem("Search Medication By Category", this::handleMedicationSearchByCategory));
        return menu;
    }

    private Menu createManageDiseasesMenu() {
        Menu menu = new Menu("Manage Diseases");
        menu.getItems().add(createMenuItem("Manage Diseases", this::handleManageDiseases));
        return menu;
    }

    private Menu createManageMedicationsMenu() {
        Menu menu = new Menu("Manage Medication");
        menu.getItems().add(createMenuItem("Manage Medication", this::handleManageMedications));
        return menu;
    }

    private MenuItem createMenuItem(String title, Runnable action) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> action.run());
        return menuItem;
    }

    private void handleManagePatients() {
        new ManagePatient(primaryStage, patients).start();
    }

    private void handleClassifyPatients() {
        new ClassificationOfPatientsByAge(primaryStage, patients).start();
    }

    private void handleManagePrescription() {
        new ManagePrescription(primaryStage, patients).start();
    }

    private void handleManageDiseases() {
        new ManageDisease(primaryStage, diseases).start();
    }

    private void handleManageMedications() {
        new ManageMedication(primaryStage, medications,this).start();
    }
    private void handleDiseaseSearch() {
        DiseaseSearch diseaseSearch = new DiseaseSearch(primaryStage);
        diseaseSearch.start();
    }

    private void handleMedicationSearch() {
        MedicationSearch medicationSearch = new MedicationSearch(primaryStage);
        medicationSearch.start();
    }

    private void handlePrescriptionSearch() {
        PrescriptionSearch prescriptionSearch = new PrescriptionSearch(primaryStage);
        prescriptionSearch.start();
    }
    private void handleMedicationSearchByCategory() {
        MedicationSearchByCategory medicationSearchByCategory = new MedicationSearchByCategory(primaryStage);
        medicationSearchByCategory.start();
    }
}
