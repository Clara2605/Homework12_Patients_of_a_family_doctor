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
        initMedications();
    }

    public Node getContent() {
        VBox rootLayout = createRootLayout();
        GridPane grid = createFormLayout();
        medicationTableView = createTableView();
        rootLayout.getChildren().addAll(grid, medicationTableView);
        rootLayout.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        return rootLayout;
    }

    private VBox createRootLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        return root;
    }

    private GridPane createFormLayout() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        setupFormFields(grid);
        setupActionButtons(grid);

        return grid;
    }

    private void setupFormFields(GridPane grid) {
        nameField = createTextField("Name:", 0, grid);
        categoryField = createTextField("Category:", 1, grid);
    }

    private TextField createTextField(String labelText, int row, GridPane grid) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        grid.add(label, 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    private void setupActionButtons(GridPane grid) {
        addButton = createButton("Add Medication", 0, grid);
        editButton = createButton("Edit Medication", 1, grid);
        deleteButton = createButton("Delete Medication", 2, grid);

        addButton.setOnAction(e -> handleAddAction());
        editButton.setDisable(true);
        editButton.setVisible(false);
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);
    }

    private Button createButton(String buttonText, int col, GridPane grid) {
        Button button = new Button(buttonText);
        grid.add(button, col, 2);
        return button;
    }

    private void handleAddAction() {
        if (validateFields()) {
            String name = nameField.getText();
            String category = categoryField.getText();
            Medication medication = new Medication(-1, name, category);

            medicationService.addMedication(medication);
            medications.add(medication);
            clearFormFields();
        }
    }

    private void clearFormFields() {
        nameField.clear();
        categoryField.clear();
    }

    private TableView<Medication> createTableView() {
        TableView<Medication> tableView = new TableView<>();
        tableView.setItems(medications);
        tableView.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Category", "category"),
                createActionsColumn());

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonStates(newSelection != null));

        return tableView;
    }

    private TableColumn<Medication, String> createColumn(String title, String property) {
        TableColumn<Medication, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private TableColumn<Medication, Void> createActionsColumn() {
        TableColumn<Medication, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Medication, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setOnAction(e -> handleEditAction(getIndex()));
                deleteBtn.setOnAction(e -> handleDeleteAction(getIndex()));
                editBtn.getStyleClass().add("edit-button");
                deleteBtn.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    HBox buttons = new HBox(editBtn, deleteBtn);
                    setGraphic(buttons);
                } else {
                    setGraphic(null);
                }
            }
        });
        return actionsColumn;
    }


    private void handleEditAction(int index) {
        Medication selectedMedication = medicationTableView.getItems().get(index);
        new EditMedicationDialog(medicationService, medicationTableView).showEditMedicationDialog(selectedMedication);
    }

    private void handleDeleteAction(int index) {
        Medication selectedMedication = medicationTableView.getItems().get(index);
        medicationService.deleteMedication(selectedMedication);
        medications.remove(selectedMedication);
    }

    private void updateButtonStates(boolean selectionExists) {
        editButton.setDisable(!selectionExists);
        deleteButton.setDisable(!selectionExists);
    }

    private boolean validateFields() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        return !name.isEmpty() && !category.isEmpty();
    }

    private void initMedications() {
        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        medications.setAll(medicationService.loadMedicationsFromDatabase());
    }
}
