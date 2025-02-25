package com.mykhailozinenko.sketchpad;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class CanvasArea extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private ScrollPane scrollPane;
    private Pane canvasContainer;
    private Pane centeringPane;
    private double lastX;
    private double lastY;
    private boolean isDrawing = false;
    private Project currentProject;

    // Default brush settings
    private BrushSettings brushSettings = new BrushSettings(Color.BLACK, 2.0);

    // Zoom properties
    private double zoomFactor = 1.0;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 4.0;
    private static final double ZOOM_DELTA = 0.1;

    public CanvasArea(Project project) {
        this.currentProject = project;
        initialize();
    }

    private void initialize() {
        // Set padding and background
        setPadding(new Insets(0));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create canvas with project's paper size
        PaperSize paperSize = currentProject.getPaperSize();
        canvas = new Canvas(paperSize.getWidthInPixels(), paperSize.getHeightInPixels());

        // Create a container for the canvas that we can apply transforms to
        canvasContainer = new Pane();
        canvasContainer.getChildren().add(canvas);
        canvasContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // The container should be exactly the size of the canvas
        canvasContainer.setPrefSize(canvas.getWidth(), canvas.getHeight());
        canvasContainer.setMinSize(canvas.getWidth(), canvas.getHeight());
        canvasContainer.setMaxSize(canvas.getWidth(), canvas.getHeight());

        // Create a pane that will center the canvas container and add padding
        centeringPane = new Pane();
        centeringPane.setPadding(new Insets(30, 0, 30, 0)); // Add top and bottom padding
        centeringPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        centeringPane.getChildren().add(canvasContainer);

        // Position the canvas container in the center of the pane
        canvasContainer.layoutXProperty().bind(
                centeringPane.widthProperty().subtract(canvasContainer.widthProperty()).divide(2));
        canvasContainer.layoutYProperty().bind(
                centeringPane.heightProperty().subtract(canvasContainer.heightProperty()).divide(2));

        // Create a scroll pane to handle scrolling
        scrollPane = new ScrollPane(centeringPane);
        scrollPane.setPannable(true); // Allow panning with mouse drag
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Add the scroll pane to this stack pane
        getChildren().add(scrollPane);

        // Get the graphics context for drawing
        gc = canvas.getGraphicsContext2D();

        // Set default properties
        gc.setStroke(brushSettings.getColor());
        gc.setLineWidth(brushSettings.getSize());
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        // Handle mouse/stylus events for drawing
        setupInputHandlers();

        // Render existing content if any
        renderProjectContent();
    }

    /**
     * Renders the current project's content to the canvas
     */
    private void renderProjectContent() {
        if (currentProject != null) {
            currentProject.getContent().render(canvas);
        }
    }

    private void setupInputHandlers() {
        // Drawing handlers
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);

        // Zoom handler (works on the scroll pane and propagates to canvas)
        this.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) { // Zoom only when Ctrl is pressed
                double delta = event.getDeltaY() > 0 ? ZOOM_DELTA : -ZOOM_DELTA;
                zoom(delta, new Point2D(event.getX(), event.getY()));
                event.consume(); // Prevent the scroll pane from also scrolling
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            scrollPane.setPannable(false);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            scrollPane.setPannable(true);
        });
    }

    /**
     * Apply zoom at the specified point
     */
    private void zoom(double delta, Point2D mousePoint) {
        double oldZoom = zoomFactor;

        // Calculate new zoom factor
        zoomFactor += delta;
        zoomFactor = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomFactor));

        // If zoom didn't change (at min/max limits), exit
        if (oldZoom == zoomFactor) return;

        // Calculate scroll position
        double scrollH = scrollPane.getHvalue();
        double scrollV = scrollPane.getVvalue();

        // Apply zoom transform to canvas container
        Scale scale = new Scale();
        scale.setX(zoomFactor);
        scale.setY(zoomFactor);

        // Update the container size to reflect the zoom
        canvasContainer.setPrefSize(canvas.getWidth() * zoomFactor, canvas.getHeight() * zoomFactor);
        canvasContainer.getTransforms().setAll(scale);

        // Try to maintain the mouse position in the view as we zoom
        Point2D scrollOffset = calculateScrollOffset(mousePoint, oldZoom, zoomFactor);
        scrollPane.setHvalue(scrollH + scrollOffset.getX());
        scrollPane.setVvalue(scrollV + scrollOffset.getY());

        // Notify any listeners that zoom has changed
        fireEvent(new ZoomEvent(zoomFactor));
    }

    /**
     * Helper method to calculate scroll offset when zooming
     */
    private Point2D calculateScrollOffset(Point2D mousePoint, double oldZoom, double newZoom) {
        double mousePosX = mousePoint.getX() / (canvas.getWidth() * oldZoom);
        double mousePosY = mousePoint.getY() / (canvas.getHeight() * oldZoom);

        double newX = mousePosX * (newZoom - oldZoom);
        double newY = mousePosY * (newZoom - oldZoom);

        return new Point2D(newX, newY);
    }

    /**
     * Increases zoom by one step
     */
    public void zoomIn() {
        // Zoom in towards the center
        zoom(ZOOM_DELTA, new Point2D(getWidth() / 2, getHeight() / 2));
    }

    /**
     * Decreases zoom by one step
     */
    public void zoomOut() {
        // Zoom out from the center
        zoom(-ZOOM_DELTA, new Point2D(getWidth() / 2, getHeight() / 2));
    }

    /**
     * Resets zoom to 100%
     */
    public void resetZoom() {
        // Get current zoom
        double delta = 1.0 - zoomFactor;

        // Apply zoom to return to 100%
        zoom(delta, new Point2D(getWidth() / 2, getHeight() / 2));
    }

    /**
     * Gets the current zoom factor
     */
    public double getZoomFactor() {
        return zoomFactor;
    }

    private void handleMousePressed(MouseEvent event) {
        // Convert screen coordinates to canvas coordinates (accounting for zoom)
        Point2D canvasPoint = convertToCanvasPoint(event.getX(), event.getY());
        lastX = canvasPoint.getX();
        lastY = canvasPoint.getY();

        isDrawing = true;

        // Store initial point as a dot operation
        DrawOperation.DotOperation dotOp = new DrawOperation.DotOperation(
                lastX, lastY,
                brushSettings.getColor(),
                brushSettings.getSize()
        );

        // Add to project content
        currentProject.addDrawOperation(dotOp);

        // Draw immediately on canvas
        dotOp.draw(gc);
    }

    private void handleMouseDragged(MouseEvent event) {
        if (!isDrawing) return;

        // Convert screen coordinates to canvas coordinates (accounting for zoom)
        Point2D canvasPoint = convertToCanvasPoint(event.getX(), event.getY());
        double currentX = canvasPoint.getX();
        double currentY = canvasPoint.getY();

        // Create a stroke operation
        DrawOperation.StrokeOperation strokeOp = new DrawOperation.StrokeOperation(
                lastX, lastY,
                currentX, currentY,
                brushSettings.getColor(),
                brushSettings.getSize()
        );

        // Add to project content
        currentProject.addDrawOperation(strokeOp);

        // Draw immediately on canvas
        strokeOp.draw(gc);

        // Update last position
        lastX = currentX;
        lastY = currentY;
    }

    /**
     * Converts mouse coordinates to canvas coordinates
     */
    private Point2D convertToCanvasPoint(double screenX, double screenY) {
        // First convert to container coordinates
        Point2D containerPoint = canvas.sceneToLocal(
                canvasContainer.localToScene(screenX, screenY));

        // Return adjusted for zoom
        return new Point2D(
                containerPoint.getX(),
                containerPoint.getY());
    }

    private void handleMouseReleased(MouseEvent event) {
        isDrawing = false;
    }

    public void clear() {
        // Clear the project content
        currentProject.clearContent();

        // Clear the canvas visually
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setBrushSettings(BrushSettings settings) {
        this.brushSettings = settings;
        gc.setStroke(settings.getColor());
        gc.setLineWidth(settings.getSize());
    }

    /**
     * Updates to show a different project
     */
    public void setProject(Project project) {
        this.currentProject = project;

        // Update canvas size to match the project's paper size
        PaperSize paperSize = project.getPaperSize();
        canvas.setWidth(paperSize.getWidthInPixels());
        canvas.setHeight(paperSize.getHeightInPixels());

        // Update the container size
        canvasContainer.setPrefSize(canvas.getWidth() * zoomFactor, canvas.getHeight() * zoomFactor);

        // Render the project content
        renderProjectContent();
    }

    // Custom event class for zoom changes
    public static class ZoomEvent extends javafx.event.Event {
        public static final javafx.event.EventType<ZoomEvent> ZOOM_CHANGED =
                new javafx.event.EventType<>(javafx.event.Event.ANY, "ZOOM_CHANGED");

        private final double zoomFactor;

        public ZoomEvent(double zoomFactor) {
            super(ZOOM_CHANGED);
            this.zoomFactor = zoomFactor;
        }

        public double getZoomFactor() {
            return zoomFactor;
        }
    }
}