package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ToolBar extends HBox {

    private ColorPicker colorPicker;
    private Slider brushSizeSlider;
    private Button clearButton;

    public ToolBar() {
        initialize();
    }

    private void initialize() {
        setPadding(new Insets(10));
        setSpacing(10);

        // Create color picker
        Label colorLabel = new Label("Color:");
        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(e -> {
            // This will be connected to the canvas later
            System.out.println("Color selected: " + colorPicker.getValue());
        });

        // Create brush size slider
        Label sizeLabel = new Label("Size:");
        brushSizeSlider = new Slider(1, 50, 2);
        brushSizeSlider.setPrefWidth(150);
        brushSizeSlider.setShowTickMarks(true);
        brushSizeSlider.setShowTickLabels(true);
        brushSizeSlider.setMajorTickUnit(10);
        brushSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // This will be connected to the canvas later
            System.out.println("Brush size: " + newVal);
        });

        // Create clear button
        clearButton = new Button("Clear Canvas");
        clearButton.setOnAction(e -> {
            // This will be connected to the canvas later
            System.out.println("Clear canvas requested");
        });

        // Create separators for visual grouping
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Add all controls to the toolbar
        getChildren().addAll(
                colorLabel, colorPicker,
                sizeLabel, brushSizeSlider,
                sep1,
                clearButton
        );
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public Slider getBrushSizeSlider() {
        return brushSizeSlider;
    }

    public Button getClearButton() {
        return clearButton;
    }
}