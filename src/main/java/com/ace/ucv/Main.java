package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Main extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private ObservableList<Disease> diseases = FXCollections.observableArrayList();
    private ObservableList<Medication> medications = FXCollections.observableArrayList();
    private ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
    }

    public void initRootLayout() {
        primaryStage.setTitle("Medic Application");
        BorderPane root = new BorderPane();
        NavigationMenu navigationMenu = new NavigationMenu(primaryStage, patients, diseases, medications, prescriptions);
        root.setTop(navigationMenu);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style2.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        CreateTable.createTable(); // Assuming this method sets up the necessary tables if they don't exist.
        primaryStage.show();
    }

    public void switchScene(BorderPane newContent) {
        // Assuming you want to change the main content of the scene
        Scene scene = new Scene(newContent, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
