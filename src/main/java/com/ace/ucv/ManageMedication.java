package com.ace.ucv;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import com.ace.ucv.services.MedicationService;
import com.ace.ucv.services.interfaces.IMedicationService;
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

public class ManageMedication {
    private NavigationMenu navigationMenu;
    private ObservableList<Medication> medications;
    private TextField nameField, categoryField;
    private TableView<Medication> medicationTableView;
    private Button addButton, editButton, deleteButton;
    private Stage primaryStage;
    private IMedicationService medicationService;

    public ManageMedication(Stage primaryStage, ObservableList<Medication> medications, NavigationMenu navigationMenu) {
        this.primaryStage = primaryStage;
        this.medications = medications;
        this.navigationMenu = navigationMenu;
        medicationService = new MedicationService();
    }

    public void start() {
        primaryStage.setTitle("Medication Management");

        VBox rootLayout = new VBox();
        rootLayout.setSpacing(10);
        rootLayout.setPadding(new Insets(10));
        rootLayout.alignmentProperty();
        rootLayout.getChildren().add(navigationMenu);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 0);

        Label categoryLabel = new Label("Category:");
        GridPane.setConstraints(categoryLabel, 0, 1);
        categoryField = new TextField();
        GridPane.setConstraints(categoryField, 1, 1);

        addButton = new Button("Add Medication");
        GridPane.setConstraints(addButton, 0, 2);
        GridPane.setColumnSpan(addButton, 2);
        addButton.setDisable(true);

        editButton = new Button("Edit Medication");
        GridPane.setConstraints(editButton, 1, 2);
        editButton.setDisable(true);
        editButton.setVisible(false);

        deleteButton = new Button("Delete Medication");
        GridPane.setConstraints(deleteButton, 2, 2);
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);

        addButton.setOnAction(e -> {
            if (validateFields()) {
                String name = nameField.getText();
                String category = categoryField.getText();
                Medication medication = new Medication(-1, name, category);

                medicationService.addMedication(medication);
                medications.add(medication);
                nameField.clear();
                categoryField.clear();
            }
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, categoryField, addButton);
        });

        categoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateAddButtonState(nameField, categoryField, addButton);
        });

        medicationTableView = new TableView<>();
        TableColumn<Medication, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Medication, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Medication, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Medication, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setTranslateX(10);

                editButton.setOnAction(e -> {
                    Medication selectedMedication = getTableView().getItems().get(getIndex());
                    showEditMedicationDialog(primaryStage, selectedMedication);
                });

                deleteButton.setOnAction(e -> {
                    Medication selectedMedication = getTableView().getItems().get(getIndex());

                    medicationService.deleteMedication(selectedMedication);
                    medications.remove(selectedMedication);
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

        medicationTableView.getColumns().addAll(nameColumn, categoryColumn, actionsColumn);
        medicationTableView.setItems(medications);
        medicationTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
        GridPane.setConstraints(categoryLabel, 0, 1);
        GridPane.setConstraints(categoryField, 1, 1);
        GridPane.setConstraints(addButton, 0, 2);
        GridPane.setConstraints(editButton, 1, 2);
        GridPane.setConstraints(deleteButton, 2, 2);
        GridPane.setConstraints(medicationTableView, 0, 3);
        GridPane.setColumnSpan(medicationTableView, 3);

        grid.getChildren().addAll(nameLabel, nameField, categoryLabel, categoryField, addButton, editButton, deleteButton, medicationTableView);



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
        //medications.setAll(Medication.loadMedicationsFromDatabase());
        medications.setAll(medicationService.loadMedicationsFromDatabase());
    }

    private boolean validateFields() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        return !name.isEmpty() && !category.isEmpty();
    }

    private void updateAddButtonState(TextField nameField, TextField categoryField, Button addButton) {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        addButton.setDisable(name.isEmpty() || category.isEmpty());
    }

    private void showEditMedicationDialog(Stage primaryStage, Medication medication) {
        Dialog<Medication> dialog = new Dialog<>();
        dialog.setTitle("Edit Medication");
        dialog.setHeaderText("Edit medication information:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setPadding(new Insets(20, 150, 10, 10));

        TextField editNameField = new TextField(medication.getName());
        TextField editCategoryField = new TextField(medication.getCategory());

        editGrid.add(new Label("Name:"), 0, 0);
        editGrid.add(editNameField, 1, 0);
        editGrid.add(new Label("Category:"), 0, 1);
        editGrid.add(editCategoryField, 1, 1);

        dialog.getDialogPane().setContent(editGrid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        editNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField, editCategoryField);
        });

        editCategoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEditButtonState(saveButton, editNameField, editCategoryField);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String editedName = editNameField.getText();
                String editedCategory = editCategoryField.getText();

                medicationService.editMedication(medication, editedName, editedCategory);
                medicationTableView.refresh();
            }
            return null;
        });

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.showAndWait();
    }

    private void updateEditButtonState(Node saveButton, TextField editNameField, TextField editCategoryField) {
        String editedName = editNameField.getText().trim();
        String editedCategory = editCategoryField.getText().trim();
        boolean isValid = !editedName.isEmpty() && !editedCategory.isEmpty();
        saveButton.setDisable(!isValid);
    }
}
