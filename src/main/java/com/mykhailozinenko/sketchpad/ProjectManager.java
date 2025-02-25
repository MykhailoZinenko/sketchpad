package com.mykhailozinenko.sketchpad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages projects for the SketchPad application
 */
public class ProjectManager {
    private static ProjectManager instance;
    private List<Project> projects;
    private Project currentProject;

    private ProjectManager() {
        projects = new ArrayList<>();
        // Create a default project if no projects exist
        currentProject = new Project("My First Project", PaperSize.A4);
        projects.add(currentProject);
    }

    /**
     * Gets the singleton instance of the ProjectManager
     */
    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    /**
     * Creates a new project and returns it
     */
    public Project createProject() {
        Project project = new Project();
        projects.add(project);
        return project;
    }

    /**
     * Creates a new project with the given name and paper size
     */
    public Project createProject(String name, PaperSize paperSize) {
        Project project = new Project(name, paperSize);
        projects.add(project);
        return project;
    }

    /**
     * Deletes the specified project
     */
    public boolean deleteProject(Project project) {
        if (projects.size() <= 1) {
            // Don't allow deleting the last project
            return false;
        }

        boolean removed = projects.remove(project);

        if (removed && project.equals(currentProject)) {
            // If we deleted the current project, switch to another one
            currentProject = projects.get(0);
        }

        return removed;
    }

    /**
     * Updates a project with new properties
     */
    public void updateProject(Project project, String name, PaperSize paperSize) {
        project.setName(name);
        project.setPaperSize(paperSize);
    }

    /**
     * Gets a project by its ID
     */
    public Optional<Project> getProject(String id) {
        return projects.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Gets the current active project
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Sets the current active project
     */
    public void setCurrentProject(Project project) {
        if (projects.contains(project)) {
            this.currentProject = project;
        }
    }

    /**
     * Gets all projects
     */
    public List<Project> getAllProjects() {
        return projects;
    }

    /**
     * Debug method to print all projects
     */
    public void printAllProjects() {
        System.out.println("All Projects:");
        for (Project p : projects) {
            System.out.println(" - " + p.getName() + " (ID: " + p.getId() + ")");
        }
    }
}