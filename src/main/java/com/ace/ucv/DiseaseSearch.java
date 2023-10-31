package com.ace.ucv;

import com.ace.ucv.controller.DiseaseSearchController;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiseaseSearch {
    private TableView<Patient> table;
    private TextField searchField;
    private Button searchButton;
    private Label countLabel;
    private DiseaseSearchController controller;
    private Stage stage;

    public DiseaseSearch(Stage stage) {
        this.stage = stage;
        this.controller = new DiseaseSearchController();
        this.table = new TableView<>();
        this.searchField = new TextField();
        this.searchButton = new Button("Search by disease");
        this.countLabel = new Label();
        setupUI();
        setupActions();
    }

    private void setupUI() {
        searchField.setPromptText("Enter the name of the disease");

        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> diseaseCol = new TableColumn<>("Disease");
        diseaseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiseaseName()));

        table.getColumns().addAll(nameCol, diseaseCol);
    }

    private void setupActions() {
        searchButton.setOnAction(e -> {
            Pair<ObservableList<Patient>, Integer> result = controller.performSearch(searchField.getText());
            table.setItems(result.getKey());
            countLabel.setText("Number of patients found: " + result.getValue());
        });
    }

    public void start() {
        VBox layout = createContent();
        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Search Diseases");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createContent() {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(searchField, searchButton, countLabel, table);
        return vbox;
    }
}
