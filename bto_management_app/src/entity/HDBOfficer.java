package entity;

import entity.enums.MaritalStatus;
import entity.enums.UserRole;

/**
 * Represents an HDB officer user in the BTO Management System.
 * Extends the Applicant class with officer-specific functionality.
 */
public class HDBOfficer extends Applicant {
    private String handlingProjectName;

    /**
     * Creates a new HDB Officer with the specified details.
     * 
     * @param nric          The NRIC of the officer
     * @param name          The name of the officer
     * @param password      The password of the officer
     * @param age           The age of the officer
     * @param maritalStatus The marital status of the officer
     */
    public HDBOfficer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        // Call the Applicant constructor but override the role to HDB_OFFICER
        super(nric, name, password, age, maritalStatus);
        setRole(UserRole.HDB_OFFICER); // Override the role set by Applicant constructor
        this.handlingProjectName = null;
    }

    /**
     * Gets the name of the project this officer is handling.
     * 
     * @return The project name, or null if not handling any project
     */
    public String getHandlingProjectName() {
        return handlingProjectName;
    }

    /**
     * Sets the name of the project this officer is handling.
     * 
     * @param projectName The new project name, or null to clear
     */
    public void setHandlingProjectName(String projectName) {
        this.handlingProjectName = projectName;
    }

    /**
     * Checks if this officer is handling a project.
     * 
     * @return true if the officer is handling a project, false otherwise
     */
    public boolean isHandlingProject() {
        return handlingProjectName != null;
    }

    /**
     * Clears the project this officer is handling.
     */
    public void clearHandlingProject() {
        this.handlingProjectName = null;
    }
}