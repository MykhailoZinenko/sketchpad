package com.mykhailozinenko.sketchpad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SketchPadApplication extends Application {

    private static final String APP_TITLE = "SketchPad";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        // Create the main UI container
        BorderPane root = new BorderPane();

        // Create and set the canvas area
        CanvasArea canvasArea = new CanvasArea();
        canvasArea.getStyleClass().add("canvas-area");
        root.setCenter(canvasArea);

        // Create and set the toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("tool-bar");
        root.setTop(toolBar);

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

        // Configure and show the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}