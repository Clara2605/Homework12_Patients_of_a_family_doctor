package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.services.DiseaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;

public class ManageDisease {
    private ObservableList<Disease> diseases;
    private TextField nameField;
    private TableView<Disease> diseaseTableView;
    private Button addButton, editButton, deleteButton;
    private Stage primaryStage;
    private DiseaseService diseaseService;

    public ManageDisease(Stage primaryStage, ObservableList<Disease> diseases) {
        this.primaryStage = primaryStage;
        this.diseases = diseases;
        this.diseaseService = new DiseaseService();
    }

    public void start() {
        primaryStage.setTitle("Manage Diseases");

        GridPane grid = new GridPane();
        grid.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 0);

        addButton = new Button("Add Disease");
        GridPane.setConstraints(addButton, 0, 1);
        GridPane.setColumnSpan(addButton, 2);
        addButton.setDisable(true);
        addButton.setPadding(new Insets(10));

        editButton = new Button("Edit Disease");
        GridPane.setConstraints(editButton, 1, 1);
        editButton.setDisable(true);
        editButton.setVisible(false);
        editButton.getStyleClass().add("edit-button");

        deleteButton = new Button("Delete Disease");
        GridPane.setConstraints(deleteButton, 2, 1);
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);
        deleteButton.getStyleClass().add("delete-button");

        deleteButton.setOnAction(e -> {
            Disease selectedDisease = diseaseTableView.getSelectionModel().getSelectedItem();
            if (selectedDisease != null) {
                //selectedDisease.deleteDisease();
                diseaseService.deleteDisease(selectedDisease);
                diseases.remove(selectedDisease);
            }
        });

        addButton.setOnAction(e -> {
            String name = nameField.getText();
            //Disease.addDisease(name);
            diseaseService.addDisease(name);

            Disease disease = new Disease(name);
            diseases.add(disease);

            nameField.clear();
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, addButton);
        });

        diseaseTableView = new TableView<>();
        TableColumn<Disease, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Disease, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Disease, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setTranslateX(10);

                editButton.setOnAction(e -> {
                    Disease selectedDisease = getTableView().getItems().get(getIndex());
                    showEditDiseaseDialog(primaryStage, selectedDisease);
                });

                deleteButton.setOnAction(e -> {
                    Disease selectedDisease = getTableView().getItems().get(getIndex());
                    //selectedDisease.deleteDisease();
                    diseaseService.deleteDisease(selectedDisease);
                    diseases.remove(selectedDisease);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        diseaseTableView.getColumns().addAll(nameColumn, actionsColumn);
        diseaseTableView.setItems(diseases);
        diseaseTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        GridPane.setConstraints(nameLabel, 0, 0);
        GridPane.setConstraints(nameField, 1, 0);
        GridPane.setConstraints(addButton, 0, 1);
        GridPane.setColumnSpan(addButton, 3);

        grid.getChildren().addAll(
                addButton, editButton, deleteButton, nameLabel, nameField, diseaseTableView
        );

        Scene scene = new Scene(grid, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection); // Pass the connection here
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, maybe show an error dialog to the user
        }
        diseases.setAll(diseaseService.loadDiseasesFromDatabase());

    }

    private void updateAddButtonState(TextField nameField, Button addButton) {
        String name = nameField.getText().trim();
        boolean isValid = !name.isEmpty() && name.matches("[a-zA-Z ]+");
        addButton.setDisable(!isValid);
    }

    private void showEditDiseaseDialog(Stage primaryStage, Disease disease) {
        Dialog<Disease> dialog = new Dialog<>();
        dialog.setTitle("Edit Disease");
        dialog.setHeaderText("Edit disease information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(disease.getName());

        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                //disease.editDisease(editedName);
                if (diseaseService == null) {
                    throw new RuntimeException(" Could not have a disease null!");
                }
                diseaseService.editDisease(disease, editedName);
                diseaseTableView.refresh();
            }
            return null;
        });
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.showAndWait();
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField) {
        String editedName = editNameField.getText().trim();
        boolean isValid = !editedName.isEmpty() && editedName.matches("[a-zA-Z ]+");
        saveButton.setDisable(!isValid);
    }
}
