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
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private IDiseaseService diseaseService;

    public ManageDisease(ObservableList<Disease> diseases) {
        this.diseases = diseases;
        this.diseaseService = new DiseaseService();
    }

   public Node getContent() {
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
                diseaseService.deleteDisease(selectedDisease);
                diseases.remove(selectedDisease);
            }
        });

        addButton.setOnAction(e -> {
            String name = nameField.getText();
            diseaseService.addDisease(name);

            Disease disease = new Disease(name);
            diseases.add(disease);

            nameField.clear();
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateAddButtonState(nameField, addButton));

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
                    new EditDiseaseDialog(diseaseService, diseaseTableView).showEditDiseaseDialog(selectedDisease);
                });

                deleteButton.setOnAction(e -> {
                    Disease selectedDisease = getTableView().getItems().get(getIndex());
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

        diseases.setAll(diseaseService.loadDiseasesFromDatabase());
        return grid;
    }

    private void updateAddButtonState(TextField nameField, Button addButton) {
        String name = nameField.getText().trim();
        boolean isValid = !name.isEmpty() && name.matches("[a-zA-Z ]+");
        addButton.setDisable(!isValid);
    }
}
