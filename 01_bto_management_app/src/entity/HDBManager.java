package entity;

import entity.enums.MaritalStatus;
import entity.enums.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB manager user in the BTO Management System.
 * Extends the User class with manager-specific functionality.
 */
public class HDBManager extends User {
    private List<String> createdProjectNames;

    /**
     * Creates a new HDB Manager with the specified details.
     * 
     * @param nric          The NRIC of the manager
     * @param name          The name of the manager
     * @param password      The password of the manager
     * @param age           The age of the manager
     * @param maritalStatus The marital status of the manager
     */
    public HDBManager(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus, UserRole.HDB_MANAGER);
        this.createdProjectNames = new ArrayList<>();
    }

    /**
     * Gets the list of projects created by this manager.
     * 
     * @return The list of project names
     */
    public List<String> getCreatedProjectNames() {
        return createdProjectNames;
    }

    /**
     * Adds a project to the list of projects created by this manager.
     * 
     * @param projectName The name of the project to add
     * @return true if the project was added, false if already present
     */
    public boolean addCreatedProject(String projectName) {
        if (!createdProjectNames.contains(projectName)) {
            createdProjectNames.add(projectName);
            return true;
        }
        return false;
    }

    /**
     * Removes a project from the list of projects created by this manager.
     * 
     * @param projectName The name of the project to remove
     * @return true if the project was removed, false if not found
     */
    public boolean removeCreatedProject(String projectName) {
        return createdProjectNames.remove(projectName);
    }

    /**
     * Checks if this manager has any active projects in the given period.
     * 
     * @param projects  The list of all projects
     * @param startDate The start date of the period
     * @param endDate   The end date of the period
     * @return true if the manager has an active project in the period, false
     *         otherwise
     */
    public boolean hasActiveProjectInPeriod(List<Project> projects,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        for (Project project : projects) {
            if (project.getManagerInChargeNric().equals(this.getNric())) {
                // Check if date ranges overlap
                if (!(project.getApplicationClosingDate().isBefore(startDate) ||
                        project.getApplicationOpeningDate().isAfter(endDate))) {
                    return true;
                }
            }
        }
        return false;
    }
}