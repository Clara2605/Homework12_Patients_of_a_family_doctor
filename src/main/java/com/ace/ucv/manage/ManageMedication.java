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

    /**
     * Constructor for ManageMedication.
     * Initializes the class with a list of medications and sets up the medication service.
     *
     * @param medications ObservableList of Medications.
     */
    public ManageMedication(ObservableList<Medication> medications) {
        this.medications = medications;
        medicationService = new MedicationService();
        initMedications();
    }

    /**
     * Creates the content of the manage medications section.
     * Sets up the layout, form fields, and table for managing medications.
     *
     * @return Node representing the layout of the manage medications section.
     */
    public Node getContent() {
        VBox rootLayout = createRootLayout();
        GridPane grid = createFormLayout();
        medicationTableView = createTableView();
        rootLayout.getChildren().addAll(grid, medicationTableView);
        rootLayout.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        return rootLayout;
    }

    /**
     * Creates and configures a VBox for the root layout.
     *
     * @return Configured VBox.
     */
    private VBox createRootLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        return root;
    }

    /**
     * Creates and configures a GridPane for the form layout.
     *
     * @return Configured GridPane.
     */
    private GridPane createFormLayout() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        setupFormFields(grid);
        setupActionButtons(grid);

        return grid;
    }

    /**
     * Sets up the form fields for adding/editing medications.
     *
     * @param grid The GridPane to add the form fields to.
     */
    private void setupFormFields(GridPane grid) {
        nameField = createTextField("Name:", 0, grid);
        categoryField = createTextField("Category:", 1, grid);
    }

    /**
     * Creates a TextField with a label.
     *
     * @param labelText Text for the label.
     * @param row The row index in the grid where the field will be placed.
     * @param grid The GridPane to add the TextField to.
     * @return The created TextField.
     */
    private TextField createTextField(String labelText, int row, GridPane grid) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        grid.add(label, 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    /**
     * Sets up action buttons for managing medications.
     *
     * @param grid The GridPane to add the buttons to.
     */
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

    /**
     * Creates a Button and places it in the grid.
     *
     * @param buttonText Text to display on the button.
     * @param col The column index in the grid where the button will be placed.
     * @param grid The GridPane to add the Button to.
     * @return The created Button.
     */
    private Button createButton(String buttonText, int col, GridPane grid) {
        Button button = new Button(buttonText);
        grid.add(button, col, 2);
        return button;
    }

    /**
     * Handles the action for adding a new medication.
     * Validates the input, adds the medication, and updates the TableView.
     */
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

    /**
     * Clears the form fields after adding or editing a medication.
     */
    private void clearFormFields() {
        nameField.clear();
        categoryField.clear();
    }

    /**
     * Creates and configures the TableView for displaying medications.
     *
     * @return Configured TableView.
     */
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

    /**
     * Creates a TableColumn for a specified property of Medication.
     *
     * @param title Title of the column.
     * @param property Property name of the Medication to bind to the column.
     * @return Configured TableColumn.
     */
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

    /**
     * Handles the action for editing a selected medication.
     *
     * @param index The index of the medication to be edited.
     */
    private void handleEditAction(int index) {
        Medication selectedMedication = medicationTableView.getItems().get(index);
        new EditMedicationDialog(medicationService, medicationTableView).showEditMedicationDialog(selectedMedication);
    }

    /**
     * Handles the action for deleting a selected medication.
     *
     * @param index The index of the medication to be deleted.
     */
    private void handleDeleteAction(int index) {
        Medication selectedMedication = medicationTableView.getItems().get(index);
        medicationService.deleteMedication(selectedMedication);
        medications.remove(selectedMedication);
    }

    /**
     * Updates the state of the edit and delete buttons based on whether a medication is selected.
     *
     * @param selectionExists True if a medication is selected, false otherwise.
     */
    private void updateButtonStates(boolean selectionExists) {
        editButton.setDisable(!selectionExists);
        deleteButton.setDisable(!selectionExists);
    }

    /**
     * Validates the input fields for adding or editing a medication.
     *
     * @return True if the input fields are valid, false otherwise.
     */
    private boolean validateFields() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        return !name.isEmpty() && !category.isEmpty();
    }

    /**
     * Initializes the medications list with data from the database.
     */
    private void initMedications() {
        try (Connection connection = DatabaseManager.connect()) {
            CreateTable.createTable(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        medications.setAll(medicationService.loadMedicationsFromDatabase());
    }
}
