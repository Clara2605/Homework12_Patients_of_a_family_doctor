package com.ace.ucv.manage;

import com.ace.ucv.model.Disease;
import com.ace.ucv.services.DiseaseService;
import com.ace.ucv.services.interfaces.IDiseaseService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ManageDisease {

    private ObservableList<Disease> diseases;
    private TextField nameField;
    private TableView<Disease> diseaseTableView;
    private IDiseaseService diseaseService;

    public ManageDisease(ObservableList<Disease> diseases) {
        this.diseases = diseases;
        this.diseaseService = new DiseaseService();
        initDiseases();
    }

    public Node getContent() {
        GridPane grid = createGridPane();
        configureNameField(grid);
        configureAddButton(grid);
        configureTableView(grid);
        return grid;
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);
        return grid;
    }

    private void configureNameField(GridPane grid) {
        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
    }

    private void configureAddButton(GridPane grid) {
        Button addButton = new Button("Add Disease");
        addButton.setDisable(true);
        addButton.setPadding(new Insets(10));
        addButton.setOnAction(e -> handleAddAction());
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(addButton));
        grid.add(addButton, 0, 1, 2, 1);
    }

    private void configureTableView(GridPane grid) {
        diseaseTableView = new TableView<>();
        TableColumn<Disease, String> nameColumn = createNameColumn();
        TableColumn<Disease, Void> actionsColumn = createActionsColumn();

        // Set the width of the columns to be a percentage of the table's width
        nameColumn.prefWidthProperty().bind(diseaseTableView.widthProperty().multiply(0.65));
        actionsColumn.prefWidthProperty().bind(diseaseTableView.widthProperty().multiply(0.3));

        diseaseTableView.getColumns().addAll(nameColumn, actionsColumn);
        diseaseTableView.setItems(diseases);

        // Bind the width of the table to the width of the parent container (GridPane)
        diseaseTableView.prefWidthProperty().bind(grid.widthProperty());

        grid.add(diseaseTableView, 0, 2, 3, 1);
    }


    private TableColumn<Disease, String> createNameColumn() {
        TableColumn<Disease, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        return nameColumn;
    }

    private TableColumn<Disease, Void> createActionsColumn() {
        TableColumn<Disease, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<Disease, Void>() {
            private final Button editButton = createStyledButton("Edit");
            private final Button deleteButton = createStyledButton("Delete");

            {
                editButton.setOnAction(e -> handleEditAction(getIndex()));
                deleteButton.setOnAction(e -> handleDeleteAction(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    HBox buttons = new HBox(editButton, deleteButton);
                    setGraphic(buttons);
                } else {
                    setGraphic(null);
                }
            }
        });
        return actionsColumn;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add(text.toLowerCase() + "-button");
        return button;
    }

    private void handleAddAction() {
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            diseaseService.addDisease(name);
            Disease disease = new Disease(name);
            diseases.add(disease);
            nameField.clear();
        }
    }

    private void handleEditAction(int index) {
        if (index >= 0 && index < diseases.size()) {
            Disease selectedDisease = diseaseTableView.getItems().get(index);
            EditDiseaseDialog editDiseaseDialog = new EditDiseaseDialog(diseaseService, diseaseTableView);
            editDiseaseDialog.showEditDiseaseDialog(selectedDisease);
        }
    }

    private void handleDeleteAction(int index) {
        if (index >= 0 && index < diseases.size()) {
            Disease selectedDisease = diseaseTableView.getItems().get(index);
            diseaseService.deleteDisease(selectedDisease);
            diseases.remove(selectedDisease);
        }
    }

    private void updateAddButtonState(Button addButton) {
        String name = nameField.getText().trim();
        boolean isValid = !name.isEmpty() && name.matches("[a-zA-Z ]+");
        addButton.setDisable(!isValid);
    }

    private void initDiseases() {
        diseases.setAll(diseaseService.loadDiseasesFromDatabase());
    }
}
