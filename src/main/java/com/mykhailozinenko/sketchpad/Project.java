package com.mykhailozinenko.sketchpad;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a SketchPad project with metadata and content
 */
public class Project {
    private String id;
    private String name;
    private PaperSize paperSize;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private ProjectContent content;

    // Create a formatter for display
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new project with default settings
     */
    public Project() {
        this.id = UUID.randomUUID().toString();
        this.name = "Untitled Project";
        this.paperSize = PaperSize.A4;
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = this.createdDate;
        this.content = new ProjectContent(paperSize);
    }

    /**
     * Creates a project with the specified name and paper size
     */
    public Project(String name, PaperSize paperSize) {
        this();
        this.name = name;
        this.paperSize = paperSize;
        this.content = new ProjectContent(paperSize);
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateLastModified();
    }

    public PaperSize getPaperSize() {
        return paperSize;
    }

    /**
     * Paper size can only be set during project creation,
     * not after the project has been created.
     * This method is restricted and should only be used internally.
     */
    protected void setPaperSize(PaperSize paperSize) {
        this.paperSize = paperSize;
        updateLastModified();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getFormattedCreatedDate() {
        return createdDate.format(DATE_FORMATTER);
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getFormattedLastModifiedDate() {
        return lastModifiedDate.format(DATE_FORMATTER);
    }

    /**
     * Gets the project content
     */
    public ProjectContent getContent() {
        return content;
    }

    /**
     * Adds a drawing operation to the project content
     */
    public void addDrawOperation(DrawOperation operation) {
        content.addOperation(operation);
        updateLastModified();
    }

    /**
     * Clears all drawing content
     */
    public void clearContent() {
        content.clear();
        updateLastModified();
    }

    /**
     * Updates the last modified date to the current time
     */
    private void updateLastModified() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name;
    }
}