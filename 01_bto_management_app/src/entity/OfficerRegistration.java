package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an HDB officer registration for a project.
 */
public class OfficerRegistration implements Serializable {
    private String registrationId;
    private String officerNric;
    private String projectName;
    private boolean isApproved;
    private LocalDateTime registrationDate;

    /**
     * Creates a new officer registration with the specified details.
     * 
     * @param registrationId The unique ID of the registration
     * @param officerNric    The NRIC of the officer
     * @param projectName    The name of the project
     */
    public OfficerRegistration(String registrationId, String officerNric, String projectName) {
        this.registrationId = registrationId;
        this.officerNric = officerNric;
        this.projectName = projectName;
        this.isApproved = false;
        this.registrationDate = LocalDateTime.now();
    }

    /**
     * Gets the ID of this registration.
     * 
     * @return The registration ID
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * Gets the NRIC of the officer.
     * 
     * @return The officer's NRIC
     */
    public String getOfficerNric() {
        return officerNric;
    }

    /**
     * Gets the name of the project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Checks if this registration has been approved.
     * 
     * @return true if the registration is approved, false otherwise
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Sets the approval status of this registration.
     * 
     * @param approved true to approve the registration, false otherwise
     */
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }

    /**
     * Gets the date and time this registration was submitted.
     * 
     * @return The registration date
     */
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
}