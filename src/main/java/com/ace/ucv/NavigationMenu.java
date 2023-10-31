package com.ace.ucv;

import com.ace.ucv.*;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication; // Asigurați-vă că importați clasa Medication
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class NavigationMenu extends MenuBar {
    public NavigationMenu(Stage primaryStage, ObservableList<Patient> patients, ObservableList<Disease> diseases, ObservableList<Medication> medications) {
        // Crearea meniului de navigare (navbar)
        Menu managePatientsMenu = new Menu("Manage Patients");
        MenuItem managePatientsMenuItem = new MenuItem("Manage Patients");
        managePatientsMenuItem.setOnAction(e -> {
            ManagePatient managePatient = new ManagePatient(primaryStage, patients);
            managePatient.start();
        });
        managePatientsMenu.getItems().add(managePatientsMenuItem);

        Menu classifyPatientsMenu = new Menu("Classify Patients by Age");
        MenuItem classifyPatientsMenuItem = new MenuItem("Classification of Patients by Age");
        classifyPatientsMenuItem.setOnAction(e -> {
            ClassificationOfPatientsByAge classifier = new ClassificationOfPatientsByAge(primaryStage, patients);
            classifier.start();
        });
        classifyPatientsMenu.getItems().add(classifyPatientsMenuItem);

        // Crearea meniului "Add Prescription"
        Menu addPrescriptionMenu = new Menu("Add Prescription");
        MenuItem addPrescriptionMenuItem = new MenuItem("Add Prescription");
        addPrescriptionMenuItem.setOnAction(e -> {
            AddPrescription addPrescription = new AddPrescription(primaryStage, patients);
            addPrescription.start(); // Alege primul pacient sau oricare alt pacient pentru a începe adăugarea de prescripții
        });
        addPrescriptionMenu.getItems().add(addPrescriptionMenuItem);

        // Crearea meniului "Manage Diseases"
        Menu manageDiseasesMenu = new Menu("Manage Diseases");
        MenuItem manageDiseasesMenuItem = new MenuItem("Manage Diseases");
        manageDiseasesMenuItem.setOnAction(e -> {
            ManageDisease manageDisease = new ManageDisease(primaryStage, diseases);
            manageDisease.start();
        });
        manageDiseasesMenu.getItems().add(manageDiseasesMenuItem);

        // Crearea meniului "Manage Medication"
        Menu manageMedicationsMenu = new Menu("Manage Medication");
        MenuItem manageMedicationsMenuItem = new MenuItem("Manage Medication");
        manageMedicationsMenuItem.setOnAction(e -> {
            ManageMedication manageMedication = new ManageMedication(primaryStage, medications);
            manageMedication.start();
        });
        manageMedicationsMenu.getItems().add(manageMedicationsMenuItem);

        this.getMenus().addAll(managePatientsMenu, classifyPatientsMenu, addPrescriptionMenu, manageDiseasesMenu, manageMedicationsMenu);
    }
}
