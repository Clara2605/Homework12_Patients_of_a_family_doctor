package com.ace.ucv.nav;

import com.ace.ucv.classification.*;
import com.ace.ucv.controller.DiseaseSearchController;
import com.ace.ucv.controller.MedicationSearchByCategoryController;
import com.ace.ucv.controller.MedicationSearchController;
import com.ace.ucv.controller.PrescriptionSearchController;
import com.ace.ucv.manage.ManageDisease;
import com.ace.ucv.manage.ManageMedication;
import com.ace.ucv.manage.ManagePatient;
import com.ace.ucv.manage.ManagePrescription;
import com.ace.ucv.model.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NavigationMenu extends MenuBar {

    private Stage primaryStage;
    private ObservableList<Patient> patients;
    private ObservableList<Disease> diseases;
    private ObservableList<Medication> medications;
    private ObservableList<Prescription> prescriptions;
    private BorderPane mainLayout;

    public NavigationMenu(Stage primaryStage, BorderPane mainLayout, ObservableList<Patient> patients, ObservableList<Disease> diseases, ObservableList<Medication> medications, ObservableList<Prescription> prescriptions) {
        this.primaryStage = primaryStage;
        this.patients = patients;
        this.diseases = diseases;
        this.medications = medications;
        this.prescriptions = prescriptions;
        initializeMenus();
        this.mainLayout = mainLayout;
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
        ManagePatient managePatient = new ManagePatient(patients);
        Node managePatientsContent = managePatient.getContent();
        changeContent(managePatientsContent);
    }

    private void handleClassifyPatients() {
        ClassificationOfPatientsByAge classificationOfPatientsByAge = new ClassificationOfPatientsByAge(patients);
        Node classificationContent = classificationOfPatientsByAge.getContent();
        changeContent(classificationContent);
    }

    private void handleManagePrescription() {
        ManagePrescription managePrescription = new ManagePrescription(patients);
        Node managePrescriptionsContent = managePrescription.getContent();
        changeContent(managePrescriptionsContent);
    }

    private void handleManageDiseases() {
        ManageDisease manageDisease = new ManageDisease(diseases);
        Node manageDiseasesContent = manageDisease.getContent();
        changeContent(manageDiseasesContent);
    }

    private void handleManageMedications() {
        ManageMedication manageMedication = new ManageMedication(medications);
        Node manageMedicationsContent = manageMedication.getContent();
        changeContent(manageMedicationsContent);
    }
    private void handleDiseaseSearch() {
        DiseaseSearch diseaseSearch = new DiseaseSearch(new DiseaseSearchController());
        Node diseaseSearchContent = diseaseSearch.getContent();
        changeContent(diseaseSearchContent);
    }

    private void handleMedicationSearch() {
        MedicationSearch medicationSearch = new MedicationSearch(new MedicationSearchController());
        Node medicationSearchContent = medicationSearch.getContent();
        changeContent(medicationSearchContent);
    }

    private void handlePrescriptionSearch() {
        PrescriptionSearch prescriptionSearch = new PrescriptionSearch(new PrescriptionSearchController());
        Node prescriptionSearchContent = prescriptionSearch.getContent();
        changeContent(prescriptionSearchContent);
    }
    private void handleMedicationSearchByCategory() {
        MedicationSearchByCategory medicationSearchByCategory = new MedicationSearchByCategory(new MedicationSearchByCategoryController());
        Node medicationSearchByCategoryContent = medicationSearchByCategory.getContent();
        changeContent(medicationSearchByCategoryContent);
    }

    private void changeContent(Node newContent) {
        mainLayout.setCenter(newContent);
    }
}