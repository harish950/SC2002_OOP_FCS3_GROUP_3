package entity;

import entity.enums.MaritalStatus;
import entity.enums.UserRole;

/**
 * Represents an applicant user in the BTO Management System.
 * Extends the User class with applicant-specific functionality.
 */
public class Applicant extends User {
    private String currentApplicationId;

    /**
     * Creates a new Applicant with the specified details.
     * 
     * @param nric          The NRIC of the applicant
     * @param name          The name of the applicant
     * @param password      The password of the applicant
     * @param age           The age of the applicant
     * @param maritalStatus The marital status of the applicant
     */
    public Applicant(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus, UserRole.APPLICANT);
        this.currentApplicationId = null;
    }

    /**
     * Gets the ID of the current application of this applicant.
     * 
     * @return The application ID, or null if no current application
     */
    public String getCurrentApplicationId() {
        return currentApplicationId;
    }

    /**
     * Sets the ID of the current application of this applicant.
     * 
     * @param applicationId The new application ID, or null to clear
     */
    public void setCurrentApplicationId(String applicationId) {
        this.currentApplicationId = applicationId;
    }

    /**
     * Checks if this applicant has a current application.
     * 
     * @return true if the applicant has an application, false otherwise
     */
    public boolean hasApplication() {
        return currentApplicationId != null;
    }

    /**
     * Clears the current application of this applicant.
     */
    public void clearApplication() {
        this.currentApplicationId = null;
    }
}