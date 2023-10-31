package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
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

        // Adăugarea imaginii de fundal
        Image backgroundImage = new Image(getClass().getResourceAsStream("/home-img.svg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(600); // Lățimea imaginii
        backgroundImageView.setFitHeight(660); // Înălțimea imaginii
        backgroundImageView.setTranslateX(50); // Poziționarea orizontală (deplasare la dreapta)
        backgroundImageView.setTranslateY(40); // Poziționarea verticală (deplasare în sus)

        root.getChildren().add(backgroundImageView);

        // Adăugarea meniului în partea de sus a container-ului
        NavigationMenu navigationMenu = new NavigationMenu(primaryStage, patients, diseases, medications);
        root.setTop(navigationMenu);

        // Restul codului pentru crearea scenei și afișarea inițială

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style2.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


        primaryStage.setScene(scene);

        CreateTable.createTable(); // Dacă este necesar (nu mai este nevoie în acest punct)

        primaryStage.show();
    }
}
