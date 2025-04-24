package data;

import entity.Project;
import entity.enums.FlatType;

import java.io.*;
// import java.time.LocalDate;
import java.util.*;

/**
 * Data access class for Project objects.
 * Handles storage and retrieval of projects.
 */
public class ProjectDB {
    private static final String PROJECT_DATA_FILE = "projects.dat";
    private Map<String, Project> projects;

    /**
     * Constructs a new ProjectDB and loads existing data if available.
     */
    public ProjectDB() {
        projects = new HashMap<>();
        loadData();
    }

    /**
     * Loads project data from file.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROJECT_DATA_FILE))) {
            projects = (Map<String, Project>) ois.readObject();
        } catch (FileNotFoundException e) {
            // System.out.println("Project data file not found. Starting with empty project
            // database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading project data: " + e.getMessage());
        }
    }

    /**
     * Saves project data to file.
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROJECT_DATA_FILE))) {
            oos.writeObject(projects);
        } catch (IOException e) {
            System.out.println("Error saving project data: " + e.getMessage());
        }
    }

    /**
     * Adds a new project to the database.
     * 
     * @param project The project to add
     * @return true if the project was added, false if project name already exists
     */
    public boolean addProject(Project project) {
        if (projects.containsKey(project.getProjectName())) {
            return false;
        }
        projects.put(project.getProjectName(), project);
        saveData();
        return true;
    }

    /**
     * Updates an existing project in the database.
     * 
     * @param project The project to update
     * @return true if the project was updated, false if project name not found
     */
    public boolean updateProject(Project project) {
        if (!projects.containsKey(project.getProjectName())) {
            return false;
        }
        projects.put(project.getProjectName(), project);
        saveData();
        return true;
    }

    /**
     * Deletes a project from the database.
     * 
     * @param projectName The name of the project to delete
     * @return true if the project was deleted, false if project name not found
     */
    public boolean deleteProject(String projectName) {
        if (!projects.containsKey(projectName)) {
            return false;
        }
        projects.remove(projectName);
        saveData();
        return true;
    }

    /**
     * Gets a project by name.
     * 
     * @param projectName The name of the project to get
     * @return The project, or null if not found
     */
    public Project getProject(String projectName) {
        return projects.get(projectName);
    }

    /**
     * Gets all projects in the database.
     * 
     * @return A list of all projects
     */
    public List<Project> getAllProjects() {
        return new ArrayList<>(projects.values());
    }

    /**
     * Gets all visible projects.
     * 
     * @return A list of all visible projects
     */
    public List<Project> getAllVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible()) {
                visibleProjects.add(project);
            }
        }
        return visibleProjects;
    }

    /**
     * Gets all projects created by a specific manager.
     * 
     * @param managerNric The NRIC of the manager
     * @return A list of projects created by the manager
     */
    public List<Project> getProjectsByManager(String managerNric) {
        List<Project> managerProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getManagerInChargeNric().equals(managerNric)) {
                managerProjects.add(project);
            }
        }
        return managerProjects;
    }

    /**
     * Gets all projects handled by a specific officer.
     * 
     * @param officerNric The NRIC of the officer
     * @return A list of projects handled by the officer
     */
    public List<Project> getProjectsByOfficer(String officerNric) {
        List<Project> officerProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getOfficerNrics().contains(officerNric)) {
                officerProjects.add(project);
            }
        }
        return officerProjects;
    }

    /**
     * Gets all projects that are open for applications by a specific marital
     * status.
     * 
     * @param isMarried Whether to filter for married applicants
     * @return A list of visible projects suitable for the marital status
     */
    public List<Project> getVisibleProjectsByMaritalStatus(boolean isMarried) {
        List<Project> filteredProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible()) {
                // For married, return all visible projects
                if (isMarried) {
                    filteredProjects.add(project);
                }
                // For singles, return projects with 2-Room flats
                else if (!isMarried && project.getFlatTypeUnits().containsKey(FlatType.TWO_ROOM)) {
                    filteredProjects.add(project);
                }
            }
        }
        return filteredProjects;
    }

    /**
     * Filters projects by neighborhood.
     * 
     * @param projects     The list of projects to filter
     * @param neighborhood The neighborhood to filter by
     * @return A filtered list of projects
     */
    public List<Project> filterByNeighborhood(List<Project> projects, String neighborhood) {
        if (neighborhood == null || neighborhood.isEmpty()) {
            return projects;
        }

        List<Project> filteredProjects = new ArrayList<>();
        for (Project project : projects) {
            if (project.getNeighborhood().equalsIgnoreCase(neighborhood)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }

    /**
     * Filters projects by flat type availability.
     * 
     * @param projects The list of projects to filter
     * @param flatType The flat type to filter by
     * @return A filtered list of projects
     */
    public List<Project> filterByFlatType(List<Project> projects, FlatType flatType) {
        if (flatType == null) {
            return projects;
        }

        List<Project> filteredProjects = new ArrayList<>();
        for (Project project : projects) {
            if (project.getFlatTypeUnits().containsKey(flatType) &&
                    project.getFlatTypeUnits().get(flatType) > 0) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }
}