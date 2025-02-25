package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Dialog for creating or editing a project with modern macOS-style UI
 */
public class ProjectDialog extends Dialog<Project> {

    private TextField nameField;
    private ComboBox<PaperSize> paperSizeComboBox;
    private Project project;

    /**
     * Creates a new dialog for project creation
     */
    public ProjectDialog(Window owner) {
        this(owner, null);
    }

    /**
     * Creates a dialog for editing an existing project
     * @param owner The owner window
     * @param project The project to edit, or null for a new project
     */
    public ProjectDialog(Window owner, Project project) {
        this.project = project;
        boolean isNewProject = (project == null);

        // Set title and styling
        setTitle(isNewProject ? "New Project" : "Edit Project");
        initOwner(owner);

        // Use a more modern look
        getDialogPane().getStyleClass().add("modern-dialog");

        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30, 40, 30, 40));

        // Create header
        Label headerLabel = new Label(isNewProject ? "Create New Project" : "Edit Project");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Create form container with modern styling
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");

        // Create project name field with label
        VBox nameContainer = new VBox(8);
        Label nameLabel = new Label("Project Name");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        nameField = new TextField();
        nameField.getStyleClass().add("modern-text-field");
        nameField.setPrefWidth(300);

        nameContainer.getChildren().addAll(nameLabel, nameField);

        // Add name field to form container
        formContainer.getChildren().add(nameContainer);

        // Only show paper size field for new projects
        if (isNewProject) {
            // Create paper size field with label
            VBox sizeContainer = new VBox(8);
            Label sizeLabel = new Label("Paper Size");
            sizeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

            paperSizeComboBox = new ComboBox<>();
            paperSizeComboBox.getStyleClass().add("modern-combo-box");
            paperSizeComboBox.setPrefWidth(300);
            paperSizeComboBox.getItems().addAll(PaperSize.getAllSizes());
            paperSizeComboBox.setValue(PaperSize.A4);

            sizeContainer.getChildren().addAll(sizeLabel, paperSizeComboBox);

            // Add paper size field to form container
            formContainer.getChildren().add(sizeContainer);
        } else {
            // For existing projects, just show paper size as text (not editable)
            VBox sizeContainer = new VBox(8);
            Label sizeLabel = new Label("Paper Size");
            sizeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

            Label sizeValueLabel = new Label(project.getPaperSize().toString());
            sizeValueLabel.getStyleClass().add("info-label");

            sizeContainer.getChildren().addAll(sizeLabel, sizeValueLabel);

            // Add paper size display to form container
            formContainer.getChildren().add(sizeContainer);
        }

        // Add all elements to the main container
        mainContainer.getChildren().addAll(headerLabel, formContainer);

        // Set the main container as the dialog content
        getDialogPane().setContent(mainContainer);

        // If editing an existing project, set initial values
        if (!isNewProject) {
            nameField.setText(project.getName());
        }

        // Create styled buttons
        ButtonType createButtonType = new ButtonType(isNewProject ? "Create" : "Save");
        ButtonType cancelButtonType = ButtonType.CANCEL;

        getDialogPane().getButtonTypes().addAll(cancelButtonType, createButtonType);

        // Style the buttons
        getDialogPane().lookupButton(createButtonType).getStyleClass().add("primary-button");
        getDialogPane().lookupButton(cancelButtonType).getStyleClass().add("secondary-button");

        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (isNewProject) {
                    // Create a new project
                    return new Project(
                            nameField.getText().trim().isEmpty() ? "Untitled Project" : nameField.getText(),
                            paperSizeComboBox.getValue()
                    );
                } else {
                    // Update existing project - only name can be changed
                    project.setName(
                            nameField.getText().trim().isEmpty() ? "Untitled Project" : nameField.getText()
                    );
                    return project;
                }
            }
            return null;
        });

        // Auto-focus the name field
        nameField.requestFocus();
    }
}