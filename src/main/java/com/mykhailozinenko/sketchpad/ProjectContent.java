package com.mykhailozinenko.sketchpad;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores drawn content for a project.
 * This implementation uses a vector-based approach to store drawing operations.
 */
public class ProjectContent {

    private List<DrawOperation> operations;
    private PaperSize paperSize;

    /**
     * Creates a new empty project content with the specified paper size
     */
    public ProjectContent(PaperSize paperSize) {
        this.paperSize = paperSize;
        this.operations = new ArrayList<>();
    }

    /**
     * Adds a new drawing operation to the content
     */
    public void addOperation(DrawOperation operation) {
        operations.add(operation);
    }

    /**
     * Clears all drawing operations
     */
    public void clear() {
        operations.clear();
    }

    /**
     * Renders all drawing operations to the provided canvas
     */
    public void render(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear canvas with white background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Render all operations
        for (DrawOperation op : operations) {
            op.draw(gc);
        }
    }

    /**
     * Gets the paper size of this content
     */
    public PaperSize getPaperSize() {
        return paperSize;
    }

    /**
     * Gets all drawing operations
     */
    public List<DrawOperation> getOperations() {
        return operations;
    }

    /**
     * Creates a snapshot of the content as an image
     */
    public WritableImage createSnapshot(Canvas canvas) {
        // First render the content
        render(canvas);

        // Then create a snapshot
        WritableImage image = new WritableImage(
                (int) canvas.getWidth(),
                (int) canvas.getHeight());
        canvas.snapshot(null, image);

        return image;
    }
}