package com.mykhailozinenko.sketchpad;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents a drawing operation that can be replayed on a canvas.
 * This is the base for vector-based storage of drawing content.
 */
public abstract class DrawOperation {

    /**
     * Draws this operation onto the provided graphics context
     */
    public abstract void draw(GraphicsContext gc);

    /**
     * Represents a line stroke drawing operation (like pencil/brush)
     */
    public static class StrokeOperation extends DrawOperation {
        private double startX;
        private double startY;
        private double endX;
        private double endY;
        private Color color;
        private double size;

        public StrokeOperation(double startX, double startY, double endX, double endY,
                               Color color, double size) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
            this.size = size;
        }

        @Override
        public void draw(GraphicsContext gc) {
            // Save current state
            Color oldStroke = (Color) gc.getStroke();
            double oldLineWidth = gc.getLineWidth();

            // Apply operation settings
            gc.setStroke(color);
            gc.setLineWidth(size);

            // Draw the line
            gc.beginPath();
            gc.moveTo(startX, startY);
            gc.lineTo(endX, endY);
            gc.stroke();

            // Restore previous state
            gc.setStroke(oldStroke);
            gc.setLineWidth(oldLineWidth);
        }
    }

    /**
     * Represents a dot/point drawing operation
     */
    public static class DotOperation extends DrawOperation {
        private double x;
        private double y;
        private Color color;
        private double size;

        public DotOperation(double x, double y, Color color, double size) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
        }

        @Override
        public void draw(GraphicsContext gc) {
            // Save current state
            Color oldFill = (Color) gc.getFill();

            // Apply operation settings
            gc.setFill(color);

            // Draw the dot
            gc.fillOval(x - size/2, y - size/2, size, size);

            // Restore previous state
            gc.setFill(oldFill);
        }
    }

    /**
     * Additional drawing operation types can be added here
     * (e.g., shapes, text, etc.)
     */
}