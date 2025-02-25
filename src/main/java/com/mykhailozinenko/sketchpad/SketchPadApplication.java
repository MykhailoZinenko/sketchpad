package com.mykhailozinenko.sketchpad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class SketchPadApplication extends Application {

    private static final String APP_TITLE = "SketchPad";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int PROJECT_LIST_WIDTH = 800;
    private static final int PROJECT_LIST_HEIGHT = 600;

    private Stage primaryStage;
    private ProjectManager projectManager;
    private CanvasArea canvasArea;
    private Project currentProject;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.projectManager = ProjectManager.getInstance();

        // Show the project list view at startup
        showProjectListView();
    }

    /**
     * Shows the project list view
     */
    private void showProjectListView() {
        // Create the project list view
        ProjectListView projectListView = new ProjectListView();

        // Set handler for project opening
        projectListView.setOnProjectOpen(project -> {
            openProject(project);
        });

        // Create scene
        Scene scene = new Scene(projectListView, PROJECT_LIST_WIDTH, PROJECT_LIST_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        // Set scene
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Debug: print all projects
        projectManager.printAllProjects();
    }

    /**
     * Opens a project and shows the editor window
     */
    private void openProject(Project project) {
        this.currentProject = project;
        projectManager.setCurrentProject(project);

        // Create the main UI container
        BorderPane root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create main content area
        BorderPane contentPane = new BorderPane();

        // Create and set the canvas area with the current project
        canvasArea = new CanvasArea(currentProject);
        canvasArea.getStyleClass().add("canvas-area");
        contentPane.setCenter(canvasArea);

        // Create and set the toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("tool-bar");

        // Add project name to toolbar
        Button projectButton = new Button(currentProject.getName());
        projectButton.getStyleClass().add("project-button");
        projectButton.setOnAction(e -> backToProjectList());

        // Add paper size label to toolbar
        Label paperSizeLabel = new Label(currentProject.getPaperSize().toString());
        paperSizeLabel.getStyleClass().add("paper-size-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the project button and paper size to the beginning of the toolbar
        toolBar.getChildren().add(0, projectButton);
        toolBar.getChildren().add(1, paperSizeLabel);
        toolBar.getChildren().add(2, spacer);

        // Add zoom controls to the end of the toolbar
        ZoomControl zoomControl = new ZoomControl(canvasArea);
        toolBar.getChildren().add(toolBar.getChildren().size(), zoomControl);

        contentPane.setTop(toolBar);

        // Set the content pane as the root's center
        root.setCenter(contentPane);

        // Connect toolbar events to canvas
        toolBar.getColorPicker().setOnAction(e -> {
            BrushSettings settings = new BrushSettings(
                    toolBar.getColorPicker().getValue(),
                    toolBar.getBrushSizeSlider().getValue()
            );
            canvasArea.setBrushSettings(settings);
        });

        toolBar.getBrushSizeSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            BrushSettings settings = new BrushSettings(
                    toolBar.getColorPicker().getValue(),
                    newVal.doubleValue()
            );
            canvasArea.setBrushSettings(settings);
        });

        toolBar.getClearButton().setOnAction(e -> canvasArea.clear());

        // Create scene and set stylesheet
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        // Add keyboard shortcuts for zooming
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case EQUALS:
                    case PLUS:
                        canvasArea.zoomIn();
                        break;
                    case MINUS:
                        canvasArea.zoomOut();
                        break;
                    case DIGIT0:
                    case NUMPAD0:
                        canvasArea.resetZoom();
                        break;
                }
            }
        });

        // Configure and show the primary stage
        primaryStage.setTitle(APP_TITLE + " - " + currentProject.getName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns to the project list view
     */
    private void backToProjectList() {
        showProjectListView();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");

        MenuItem newProjectItem = new MenuItem("New Project");
        newProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newProjectItem.setOnAction(e -> createNewProject());

        MenuItem backToProjectsItem = new MenuItem("Projects List");
        backToProjectsItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        backToProjectsItem.setOnAction(e -> backToProjectList());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(
                newProjectItem,
                backToProjectsItem,
                new SeparatorMenuItem(),
                exitItem
        );

        // Edit menu
        Menu editMenu = new Menu("Edit");

        MenuItem clearItem = new MenuItem("Clear Canvas");
        clearItem.setOnAction(e -> canvasArea.clear());

        editMenu.getItems().addAll(clearItem);

        // View menu
        Menu viewMenu = new Menu("View");

        MenuItem zoomInItem = new MenuItem("Zoom In");
        zoomInItem.setAccelerator(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN));
        zoomInItem.setOnAction(e -> canvasArea.zoomIn());

        MenuItem zoomOutItem = new MenuItem("Zoom Out");
        zoomOutItem.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));
        zoomOutItem.setOnAction(e -> canvasArea.zoomOut());

        MenuItem resetZoomItem = new MenuItem("Reset Zoom");
        resetZoomItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN));
        resetZoomItem.setOnAction(e -> canvasArea.resetZoom());

        viewMenu.getItems().addAll(
                zoomInItem,
                zoomOutItem,
                resetZoomItem
        );

        // Project menu
        Menu projectMenu = new Menu("Project");

        MenuItem projectSettingsItem = new MenuItem("Project Settings");
        projectSettingsItem.setOnAction(e -> editCurrentProject());

        projectMenu.getItems().addAll(projectSettingsItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, projectMenu);

        return menuBar;
    }

    /**
     * Creates a new project
     */
    private void createNewProject() {
        ProjectDialog dialog = new ProjectDialog(primaryStage);
        dialog.showAndWait().ifPresent(project -> {
            // Add to project manager
            projectManager.getAllProjects().add(project);
            // Open the new project
            openProject(project);
        });
    }

    /**
     * Opens the project edit dialog for the current project
     */
    private void editCurrentProject() {
        ProjectDialog dialog = new ProjectDialog(primaryStage, currentProject);
        dialog.showAndWait().ifPresent(project -> {
            // Update UI to reflect changes
            primaryStage.setTitle(APP_TITLE + " - " + project.getName());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}