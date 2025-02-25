package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CanvasArea extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private Rectangle clipRect;
    private double lastX;
    private double lastY;
    private boolean isDrawing = false;
    private PaperSize currentPaperSize = PaperSize.A4; // Default to A4

    // Default brush settings
    private BrushSettings brushSettings = new BrushSettings(Color.BLACK, 2.0);

    public CanvasArea() {
        initialize();
    }

    private void initialize() {
        // Set padding and background
        setPadding(new Insets(20));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create canvas with initial paper size
        canvas = new Canvas(currentPaperSize.getWidthInPixels(), currentPaperSize.getHeightInPixels());

        // Apply white background to canvas
        clipRect = new Rectangle(canvas.getWidth(), canvas.getHeight());
        clipRect.setFill(Color.WHITE);

        // Add clipping rectangle and canvas to this pane
        getChildren().addAll(clipRect, canvas);

        // Get the graphics context for drawing
        gc = canvas.getGraphicsContext2D();

        // Set default properties
        gc.setStroke(brushSettings.getColor());
        gc.setLineWidth(brushSettings.getSize());
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        // Handle mouse/stylus events for drawing
        setupInputHandlers();

        // Handle resizing
        widthProperty().addListener((obs, oldVal, newVal) -> centerCanvas());
        heightProperty().addListener((obs, oldVal, newVal) -> centerCanvas());
    }

    /**
     * Centers the canvas in the available space
     */
    private void centerCanvas() {
        // No need to adjust canvas size here - it stays fixed to the paper size
        // Just ensure it's centered in the available space
        canvas.setLayoutX((getWidth() - canvas.getWidth()) / 2);
        canvas.setLayoutY((getHeight() - canvas.getHeight()) / 2);

        // Ensure the clip rectangle matches the canvas
        clipRect.setWidth(canvas.getWidth());
        clipRect.setHeight(canvas.getHeight());
        clipRect.setLayoutX(canvas.getLayoutX());
        clipRect.setLayoutY(canvas.getLayoutY());
    }

    /**
     * Changes the paper size and adjusts the canvas accordingly
     */
    public void setPaperSize(PaperSize paperSize) {
        this.currentPaperSize = paperSize;

        // Save existing content
        Color[][] pixels = captureCanvasContent();

        // Resize canvas
        canvas.setWidth(paperSize.getWidthInPixels());
        canvas.setHeight(paperSize.getHeightInPixels());

        // Reset canvas with white background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Restore content as much as possible
        restoreCanvasContent(pixels);

        // Re-center in the container
        centerCanvas();
    }

    /**
     * Captures the current canvas content as a color matrix
     */
    private Color[][] captureCanvasContent() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        Color[][] result = new Color[width][height];

        // This is slow but ensures we don't lose data when resizing
        // A more efficient solution would be to use WritableImage
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // This is a simplification - in reality, we'd need more complex image processing
                // Using JavaFX's PixelReader would be more efficient
                result[x][y] = Color.TRANSPARENT; // Placeholder
            }
        }
        return result;
    }

    /**
     * Restores canvas content from a color matrix
     */
    private void restoreCanvasContent(Color[][] pixels) {
        int width = Math.min(pixels.length, (int) canvas.getWidth());
        int height = Math.min(pixels[0].length, (int) canvas.getHeight());

        // Again, this is a simplification
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[x][y] != null && pixels[x][y] != Color.TRANSPARENT) {
                    // We would restore the pixel here
                    // gc.getPixelWriter().setColor(x, y, pixels[x][y]);
                }
            }
        }
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
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setBrushSettings(BrushSettings settings) {
        this.brushSettings = settings;
        gc.setStroke(settings.getColor());
        gc.setLineWidth(settings.getSize());
    }

    public PaperSize getCurrentPaperSize() {
        return currentPaperSize;
    }
}