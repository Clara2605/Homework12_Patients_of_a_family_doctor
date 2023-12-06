package com.ace.ucv;

import com.ace.ucv.nav.NavigationMenu;
import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;

public class Main extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private ObservableList<Disease> diseases = FXCollections.observableArrayList();
    private ObservableList<Medication> medications = FXCollections.observableArrayList();

    private Stage primaryStage;
    private BorderPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
    }

    public void initRootLayout() {
        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection); // Updated method call with connection
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, maybe show an error dialog to the user
        }

        primaryStage.setTitle("Medic Application");
        root = new BorderPane();
        NavigationMenu navigationMenu = new NavigationMenu( root, patients, diseases, medications);
        root.setTop(navigationMenu);

        Scene scene = new Scene(root, 720, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style2.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
