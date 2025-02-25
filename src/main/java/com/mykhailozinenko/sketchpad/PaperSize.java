package com.mykhailozinenko.sketchpad;

import java.util.Arrays;
import java.util.List;

/**
 * Defines standard paper sizes for the canvas.
 * Sizes are in millimeters, which will be converted to pixels.
 */
public enum PaperSize {
    A3(297, 420, "A3"),
    A4(210, 297, "A4"),
    A5(148, 210, "A5"),
    A6(105, 148, "A6");

    private final double widthMm;
    private final double heightMm;
    private final String displayName;

    // DPI (dots per inch) - standard screen resolution
    private static final double DEFAULT_DPI = 96.0;

    // Conversion factor from mm to inches
    private static final double MM_TO_INCHES = 0.0393701;

    PaperSize(double widthMm, double heightMm, String displayName) {
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.displayName = displayName;
    }

    /**
     * Converts millimeters to pixels based on the provided DPI.
     */
    private static double mmToPixels(double mm, double dpi) {
        return mm * MM_TO_INCHES * dpi;
    }

    /**
     * Gets the width of this paper size in pixels.
     */
    public double getWidthInPixels() {
        return mmToPixels(widthMm, DEFAULT_DPI);
    }

    /**
     * Gets the height of this paper size in pixels.
     */
    public double getHeightInPixels() {
        return mmToPixels(heightMm, DEFAULT_DPI);
    }

    /**
     * Gets the display name for this paper size.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns all available paper sizes as a list.
     */
    public static List<PaperSize> getAllSizes() {
        return Arrays.asList(PaperSize.values());
    }

    @Override
    public String toString() {
        return displayName;
    }
}