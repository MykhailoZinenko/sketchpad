package com.mykhailozinenko.sketchpad;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CanvasArea extends Pane {

    private Canvas canvas;
    private GraphicsContext gc;
    private double lastX;
    private double lastY;
    private boolean isDrawing = false;

    // Default brush settings
    private BrushSettings brushSettings = new BrushSettings(Color.BLACK, 2.0);

    public CanvasArea() {
        initialize();
    }

    private void initialize() {
        // Create canvas that fills the entire area
        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // Add the canvas to this pane
        getChildren().add(canvas);

        // Get the graphics context for drawing
        gc = canvas.getGraphicsContext2D();

        // Set default properties
        gc.setStroke(brushSettings.getColor());
        gc.setLineWidth(brushSettings.getSize());
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        // Handle mouse/stylus events for drawing
        setupInputHandlers();
    }

    private void setupInputHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        isDrawing = true;

        // Mark the starting point
        gc.beginPath();
        gc.moveTo(lastX, lastY);
        gc.stroke();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (!isDrawing) return;

        double currentX = event.getX();
        double currentY = event.getY();

        // Draw line from last position to current position
        gc.beginPath();
        gc.moveTo(lastX, lastY);
        gc.lineTo(currentX, currentY);
        gc.stroke();

        // Update last position
        lastX = currentX;
        lastY = currentY;
    }

    private void handleMouseReleased(MouseEvent event) {
        isDrawing = false;
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setBrushSettings(BrushSettings settings) {
        this.brushSettings = settings;
        gc.setStroke(settings.getColor());
        gc.setLineWidth(settings.getSize());
    }
}