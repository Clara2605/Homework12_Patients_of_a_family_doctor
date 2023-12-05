package com.ace.ucv.manage;

import com.ace.ucv.db.CreateTable;
import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Medication;
import com.ace.ucv.services.MedicationService;
import com.ace.ucv.services.interfaces.IMedicationService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.Connection;

public class ManageMedication {
    private ObservableList<Medication> medications;
    private TextField nameField;
    private TextField categoryField;
    private TableView<Medication> medicationTableView;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private IMedicationService medicationService;

    public ManageMedication(ObservableList<Medication> medications) {
        this.medications = medications;
        medicationService = new MedicationService();
    }

    public Node getContent() {

        VBox rootLayout = new VBox();
        rootLayout.setSpacing(10);
        rootLayout.setPadding(new Insets(10));
        rootLayout.alignmentProperty();

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

        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, categoryField, addButton));

        categoryField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, categoryField, addButton));

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
                    new EditMedicationDialog(medicationService, medicationTableView).showEditMedicationDialog(selectedMedication);
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

        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        medications.setAll(medicationService.loadMedicationsFromDatabase());
        return grid;
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
}