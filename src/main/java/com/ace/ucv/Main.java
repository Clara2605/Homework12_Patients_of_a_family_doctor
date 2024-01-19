package com.ace.ucv;

import com.ace.ucv.nav.NavigationMenu;
import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;

public class Main extends Application {
    private static final String APPLICATION_TITLE = "Medic Application";
    private static final String WELCOME_MESSAGE = "Welcome to the Medic Application!";
    private static final String WELCOME_LABEL_ID = "welcomeLabel";
    private static final String WELCOME_MESSAGE_STYLE_CLASS = "welcome-message";
    private static final String IMAGE_VIEW_STYLE_CLASS = "image-view";
    private static final String IMAGE_PATH = "img/Medical prescription-rafiki.png";
    private static final double IMAGE_VIEW_WIDTH = 650;
    private static final String STYLESHEET_PATH = "/css/style.css";
    private static final int SCENE_WIDTH = 720;
    private static final int SCENE_HEIGHT = 700;
    private static final int VBOX_SPACING = -80;

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
        initializeDatabase();
        initRootLayout();
    }

    private void initializeDatabase() {
        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, maybe show an error dialog to the user
        }
    }

    private void initRootLayout() {
        primaryStage.setTitle(APPLICATION_TITLE);
        root = new BorderPane();
        NavigationMenu navigationMenu = new NavigationMenu(root, patients, diseases, medications);
        root.setTop(navigationMenu);
        root.setCenter(setupCenterContent());

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        setupStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("img/logo28-removebg-preview.png"));
        primaryStage.show();
    }

    private VBox setupCenterContent() {
        Label welcomeLabel = new Label(WELCOME_MESSAGE);
        welcomeLabel.setId(WELCOME_LABEL_ID);
        welcomeLabel.getStyleClass().add(WELCOME_MESSAGE_STYLE_CLASS);

        ImageView imageView = setupImageView();

        VBox centerContent = new VBox(VBOX_SPACING);
        centerContent.getChildren().addAll(welcomeLabel, imageView);
        centerContent.setAlignment(Pos.CENTER);

        return centerContent;
    }

    private ImageView setupImageView() {
        ImageView imageView = new ImageView();
        Image image = new Image(IMAGE_PATH);
        imageView.setImage(image);
        imageView.setFitWidth(IMAGE_VIEW_WIDTH);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add(IMAGE_VIEW_STYLE_CLASS);

        return imageView;
    }

    private void setupStyles(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
    }
}
