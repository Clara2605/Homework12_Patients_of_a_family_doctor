package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Main extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Medic Application");

        // Crearea unui container principal (BorderPane)
        BorderPane root = new BorderPane();

        // Adăugarea meniului în partea de sus a container-ului
        NavigationMenu navigationMenu = new NavigationMenu(primaryStage, patients);
        root.setTop(navigationMenu);

        // Restul codului pentru crearea scenei și afișarea inițială

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

        CreateTable.createTable(); // Dacă este necesar (nu mai este nevoie în acest punct)

        primaryStage.show();
    }
}
