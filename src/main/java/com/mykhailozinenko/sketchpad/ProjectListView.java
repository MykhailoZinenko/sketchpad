package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Modern macOS-style project list view shown at startup
 */
public class ProjectListView extends BorderPane {

    private ListView<Project> projectListView;
    private ProjectManager projectManager;
    private Button newProjectButton;

    public ProjectListView() {
        projectManager = ProjectManager.getInstance();

        // Set up the header
        setupHeader();

        // Set up the project list
        setupProjectList();

        // Load projects when the component is created
        updateProjectList();

        // Refresh the list when the component becomes visible
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((prop, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.onShownProperty().addListener((prop2, oldVal, newVal) -> {
                            updateProjectList();
                        });
                    }
                });
            }
        });
    }

    private void setupHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 10, 30));
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("SketchPad Projects");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        newProjectButton = new Button("New Project");
        newProjectButton.getStyleClass().add("primary-button");
        newProjectButton.setOnAction(e -> createNewProject());

        header.getChildren().addAll(titleLabel, spacer, newProjectButton);

        setTop(header);
    }

    private void setupProjectList() {
        projectListView = new ListView<>();
        projectListView.getStyleClass().add("project-list-view");

        projectListView.setCellFactory(listView -> new ProjectListCell());

        // Add the list to a scroll pane
        ScrollPane scrollPane = new ScrollPane(projectListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(0, 30, 30, 30));
        contentBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        setCenter(contentBox);

        // Handle double-click to open project
        projectListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !projectListView.getSelectionModel().isEmpty()) {
                openSelectedProject();
            }
        });
    }

    private void updateProjectList() {
        projectListView.getItems().clear();
        projectListView.getItems().addAll(projectManager.getAllProjects());
        System.out.println("Project list updated. Projects count: " + projectManager.getAllProjects().size());
    }

    private void createNewProject() {
        ProjectDialog dialog = new ProjectDialog(getScene().getWindow());
        Optional<Project> result = dialog.showAndWait();

        result.ifPresent(project -> {
            projectManager.getAllProjects().add(project);
            updateProjectList();
            projectListView.getSelectionModel().select(project);
            openProject(project);
        });
    }

    private void editProject(Project project) {
        ProjectDialog dialog = new ProjectDialog(getScene().getWindow(), project);
        dialog.showAndWait().ifPresent(updatedProject -> {
            updateProjectList();
            projectListView.getSelectionModel().select(updatedProject);
        });
    }

    private void deleteProject(Project project) {
        if (projectManager.deleteProject(project)) {
            updateProjectList();
        }
    }

    private void openSelectedProject() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            openProject(selectedProject);
        }
    }

    private void openProject(Project project) {
        projectManager.setCurrentProject(project);

        // Notify about the project opening
        if (onProjectOpenHandler != null) {
            onProjectOpenHandler.handle(project);
        }
    }

    /**
     * Custom ListCell for projects with modern styling
     */
    private class ProjectListCell extends ListCell<Project> {
        private VBox content;
        private Label nameLabel;
        private Label dateLabel;
        private Button menuButton;
        private ContextMenu contextMenu;

        public ProjectListCell() {
            // Create cell content
            content = new VBox(5);
            content.setPadding(new Insets(12, 15, 12, 15));
            content.getStyleClass().add("project-cell");

            // Create project name label
            nameLabel = new Label();
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

            // Create date label
            dateLabel = new Label();
            dateLabel.getStyleClass().add("date-label");

            // Create menu button
            menuButton = new Button("⋮");
            menuButton.getStyleClass().add("menu-button");

            // Create context menu
            contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");

            editItem.setOnAction(e -> editProject(getItem()));
            deleteItem.setOnAction(e -> deleteProject(getItem()));

            contextMenu.getItems().addAll(editItem, new SeparatorMenuItem(), deleteItem);

            // Create a container for the content and menu
            HBox container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);

            VBox textContainer = new VBox(5);
            textContainer.getChildren().addAll(nameLabel, dateLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            container.getChildren().addAll(textContainer, spacer, menuButton);

            content.getChildren().add(container);

            // Set menu button action
            menuButton.setOnAction(e -> {
                contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0);
            });

            // Handle clicks on the cell to open the project
            content.setOnMouseClicked(event -> {
                if (getItem() != null) {
                    if (event.getClickCount() == 2) {
                        openProject(getItem());
                    }
                }
                event.consume();
            });

            // Set empty default
            setGraphic(null);
        }

        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
                setGraphic(null);
            } else {
                nameLabel.setText(project.getName());
                dateLabel.setText(project.getFormattedCreatedDate());

                setGraphic(content);
            }
        }
    }

    // Define a functional interface for project open handling
    @FunctionalInterface
    public interface ProjectOpenHandler {
        void handle(Project project);
    }

    private ProjectOpenHandler onProjectOpenHandler;

    /**
     * Sets a handler for project open events
     */
    public void setOnProjectOpen(ProjectOpenHandler handler) {
        this.onProjectOpenHandler = handler;
    }
}