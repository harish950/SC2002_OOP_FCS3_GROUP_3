package controller;

import data.ProjectDB;
import data.UserDB;
import entity.*;
import entity.enums.FlatType;
import entity.enums.UserRole;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing project data and operations.
 */
public class ProjectController {
    private ProjectDB projectDB;
    private UserDB userDB;

    /**
     * Constructs a new ProjectController with references to the project and user
     * databases.
     * 
     * @param projectDB The project database
     * @param userDB    The user database
     */
    public ProjectController(ProjectDB projectDB, UserDB userDB) {
        this.projectDB = projectDB;
        this.userDB = userDB;
    }

    /**
     * Creates a new project in the system.
     * 
     * @param projectName            The name of the project
     * @param neighborhood           The neighborhood of the project
     * @param twoRoomUnits           The number of 2-Room units
     * @param threeRoomUnits         The number of 3-Room units
     * @param applicationOpeningDate The opening date for applications
     * @param applicationClosingDate The closing date for applications
     * @param managerNric            The NRIC of the manager in charge
     * @param availableOfficerSlots  The number of available HDB officer slots
     * @return true if the project was created, false if project name already exists
     */
    public boolean createProject(String projectName, String neighborhood,
            int twoRoomUnits, int threeRoomUnits,
            LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
            String managerNric, int availableOfficerSlots) {
        // Check if manager exists and is a manager
        User user = userDB.getUser(managerNric);
        if (user == null || user.getRole() != UserRole.HDB_MANAGER) {
            return false;
        }

        // Create the project
        Project project = new Project(projectName, neighborhood, applicationOpeningDate,
                applicationClosingDate, managerNric, availableOfficerSlots);

        // Set flat type units
        Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
        flatTypeUnits.put(FlatType.TWO_ROOM, twoRoomUnits);
        flatTypeUnits.put(FlatType.THREE_ROOM, threeRoomUnits);

        for (Map.Entry<FlatType, Integer> entry : flatTypeUnits.entrySet()) {
            project.setFlatTypeUnits(entry.getKey(), entry.getValue());
        }

        // Add the project to the database
        boolean success = projectDB.addProject(project);

        // Update manager's created projects list
        if (success && user instanceof HDBManager) {
            ((HDBManager) user).addCreatedProject(projectName);
            userDB.updateUser(user);
        }

        return success;
    }

    /**
     * Updates an existing project in the system.
     * 
     * @param projectName            The name of the project
     * @param neighborhood           The neighborhood of the project
     * @param twoRoomUnits           The number of 2-Room units
     * @param threeRoomUnits         The number of 3-Room units
     * @param applicationOpeningDate The opening date for applications
     * @param applicationClosingDate The closing date for applications
     * @param availableOfficerSlots  The number of available HDB officer slots
     * @return true if the project was updated, false if project name not found
     */
    public boolean updateProject(String projectName, String neighborhood,
            int twoRoomUnits, int threeRoomUnits,
            LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
            int availableOfficerSlots) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return false;
        }

        // Update project details
        project.setNeighborhood(neighborhood);
        project.setFlatTypeUnits(FlatType.TWO_ROOM, twoRoomUnits);
        project.setFlatTypeUnits(FlatType.THREE_ROOM, threeRoomUnits);
        project.setApplicationOpeningDate(applicationOpeningDate);
        project.setApplicationClosingDate(applicationClosingDate);
        project.setAvailableOfficerSlots(availableOfficerSlots);

        return projectDB.updateProject(project);
    }

    /**
     * Deletes a project from the system.
     * 
     * @param projectName The name of the project to delete
     * @return true if the project was deleted, false if project name not found
     */
    public boolean deleteProject(String projectName) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return false;
        }

        // Remove project from manager's created projects list
        User user = userDB.getUser(project.getManagerInChargeNric());
        if (user instanceof HDBManager) {
            ((HDBManager) user).removeCreatedProject(projectName);
            userDB.updateUser(user);
        }

        // Remove project from officers' handling projects
        for (String officerNric : project.getOfficerNrics()) {
            User officerUser = userDB.getUser(officerNric);
            if (officerUser instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) officerUser;
                if (projectName.equals(officer.getHandlingProjectName())) {
                    officer.setHandlingProjectName(null);
                    userDB.updateUser(officer);
                }
            }
        }

        return projectDB.deleteProject(projectName);
    }

    /**
     * Gets a project by name.
     * 
     * @param projectName The name of the project to get
     * @return The project, or null if not found
     */
    public Project getProject(String projectName) {
        return projectDB.getProject(projectName);
    }

    /**
     * Gets all projects in the system.
     * 
     * @return A list of all projects
     */
    public List<Project> getAllProjects() {
        return projectDB.getAllProjects();
    }

    /**
     * Gets all visible projects.
     * 
     * @return A list of all visible projects
     */
    public List<Project> getAllVisibleProjects() {
        return projectDB.getAllVisibleProjects();
    }

    /**
     * Gets all projects created by a specific manager.
     * 
     * @param managerNric The NRIC of the manager
     * @return A list of projects created by the manager
     */
    public List<Project> getProjectsByManager(String managerNric) {
        return projectDB.getProjectsByManager(managerNric);
    }

    /**
     * Gets all projects handled by a specific officer.
     * 
     * @param officerNric The NRIC of the officer
     * @return A list of projects handled by the officer
     */
    public List<Project> getProjectsByOfficer(String officerNric) {
        return projectDB.getProjectsByOfficer(officerNric);
    }

    /**
     * Gets all projects that are open for applications by a specific marital
     * status.
     * 
     * @param isMarried Whether to filter for married applicants
     * @return A list of visible projects suitable for the marital status
     */
    public List<Project> getVisibleProjectsByMaritalStatus(boolean isMarried) {
        return projectDB.getVisibleProjectsByMaritalStatus(isMarried);
    }

    /**
     * Toggles the visibility of a project.
     * 
     * @param projectName The name of the project
     * @param visible     Whether the project should be visible
     * @return true if the visibility was toggled, false if project name not found
     */
    public boolean toggleProjectVisibility(String projectName, boolean visible) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return false;
        }

        project.setVisible(visible);
        return projectDB.updateProject(project);
    }

    /**
     * Registers an officer to handle a project.
     * 
     * @param officerNric The NRIC of the officer
     * @param projectName The name of the project
     * @return A registration ID if successful, null otherwise
     */
    public String registerOfficerForProject(String officerNric, String projectName) {
        // Check if officer and project exist
        User user = userDB.getUser(officerNric);
        Project project = projectDB.getProject(projectName);

        if (user == null || !(user instanceof HDBOfficer) || project == null) {
            return null;
        }

        // Check if officer already handles a project in this application period
        HDBOfficer officer = (HDBOfficer) user;
        if (officer.isHandlingProject()) {
            Project handlingProject = projectDB.getProject(officer.getHandlingProjectName());
            if (handlingProject != null && handlingProject.isInApplicationPeriod() &&
                    project.isInApplicationPeriod()) {
                return null;
            }
        }

        // Check if there are available slots
        if (project.getAvailableOfficerSlots() <= 0) {
            return null;
        }

        return UUID.randomUUID().toString();
    }

    /**
     * Approves an officer's registration to handle a project.
     * 
     * @param registrationId The ID of the registration
     * @param officerNric    The NRIC of the officer
     * @param projectName    The name of the project
     * @return true if the registration was approved, false otherwise
     */
    public boolean approveOfficerRegistration(String registrationId, String officerNric, String projectName) {
        // Check if officer and project exist
        User user = userDB.getUser(officerNric);
        Project project = projectDB.getProject(projectName);

        if (user == null || !(user instanceof HDBOfficer) || project == null) {
            return false;
        }

        // Check if there are available slots
        if (project.getAvailableOfficerSlots() <= 0) {
            return false;
        }

        // Update officer and project
        HDBOfficer officer = (HDBOfficer) user;
        officer.setHandlingProjectName(projectName);
        userDB.updateUser(officer);

        project.addOfficerNric(officerNric);
        project.decrementAvailableOfficerSlots();
        projectDB.updateProject(project);

        return true;
    }

    /**
     * Rejects an officer's registration to handle a project.
     * 
     * @param registrationId The ID of the registration
     * @return true if the registration was rejected, false otherwise
     */
    public boolean rejectOfficerRegistration(String registrationId) {
        // Since OfficerRegistration is not persisted in the current implementation,
        // simply return true to indicate success
        return true;
    }

    /**
     * Decrements the number of units for a specific flat type in a project.
     * 
     * @param projectName The name of the project
     * @param flatType    The flat type to decrement
     * @return true if successful, false if no more units available
     */
    public boolean decrementFlatTypeUnits(String projectName, FlatType flatType) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return false;
        }

        boolean success = project.decrementFlatTypeUnits(flatType);
        if (success) {
            projectDB.updateProject(project);
        }
        return success;
    }

    /**
     * Increments the number of units for a specific flat type in a project.
     * 
     * @param projectName The name of the project
     * @param flatType    The flat type to increment
     * @return true if successful, false if project not found
     */
    public boolean incrementFlatTypeUnits(String projectName, FlatType flatType) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return false;
        }

        project.incrementFlatTypeUnits(flatType);
        return projectDB.updateProject(project);
    }

    /**
     * Filters projects by neighborhood.
     * 
     * @param projects     The list of projects to filter
     * @param neighborhood The neighborhood to filter by
     * @return A filtered list of projects
     */
    public List<Project> filterByNeighborhood(List<Project> projects, String neighborhood) {
        return projectDB.filterByNeighborhood(projects, neighborhood);
    }

    /**
     * Filters projects by flat type availability.
     * 
     * @param projects The list of projects to filter
     * @param flatType The flat type to filter by
     * @return A filtered list of projects
     */
    public List<Project> filterByFlatType(List<Project> projects, FlatType flatType) {
        return projectDB.filterByFlatType(projects, flatType);
    }
}