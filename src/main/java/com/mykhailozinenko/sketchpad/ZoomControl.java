package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UI control for zoom operations
 */
public class ZoomControl extends HBox {

    private Button zoomInButton;
    private Button zoomOutButton;
    private Button resetZoomButton;
    private Label zoomLabel;

    private CanvasArea canvasArea;

    /**
     * Creates a new zoom control panel
     */
    public ZoomControl(CanvasArea canvasArea) {
        this.canvasArea = canvasArea;
        initialize();
    }

    private void initialize() {
        setSpacing(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 10, 0, 0));

        // Create zoom out button
        zoomOutButton = new Button("−");
        zoomOutButton.getStyleClass().add("zoom-button");
        zoomOutButton.setOnAction(e -> {
            canvasArea.zoomOut();
            updateZoomLabel();
        });

        // Create zoom label
        zoomLabel = new Label("100%");
        zoomLabel.getStyleClass().add("zoom-label");
        zoomLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        zoomLabel.setMinWidth(50);
        zoomLabel.setAlignment(Pos.CENTER);

        // Create reset zoom button (shown as the percentage)
        resetZoomButton = new Button("Reset");
        resetZoomButton.getStyleClass().add("zoom-reset-button");
        resetZoomButton.setOnAction(e -> {
            canvasArea.resetZoom();
            updateZoomLabel();
        });

        // Create zoom in button
        zoomInButton = new Button("+");
        zoomInButton.getStyleClass().add("zoom-button");
        zoomInButton.setOnAction(e -> {
            canvasArea.zoomIn();
            updateZoomLabel();
        });

        // Add all elements to the layout
        getChildren().addAll(zoomOutButton, zoomLabel, zoomInButton, resetZoomButton);

        // Initial update of the zoom label
        updateZoomLabel();
    }

    /**
     * Updates the zoom level label with the current zoom factor
     */
    private void updateZoomLabel() {
        int zoomPercent = (int) Math.round(canvasArea.getZoomFactor() * 100);
        zoomLabel.setText(zoomPercent + "%");
    }
}