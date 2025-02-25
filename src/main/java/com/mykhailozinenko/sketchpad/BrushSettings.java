package com.mykhailozinenko.sketchpad;

import javafx.scene.paint.Color;

public class BrushSettings {
    private Color color;
    private double size;

    public BrushSettings(Color color, double size) {
        this.color = color;
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}