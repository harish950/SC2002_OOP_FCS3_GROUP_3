package entity;

import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a BTO application in the system.
 */
public class Application implements Serializable {
    private String applicationId;
    private String applicantNric;
    private String projectName;
    private FlatType flatType;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private FlatBooking flatBooking;
    private Boolean withdrawn;

    /**
     * Creates a new application with the specified details.
     * 
     * @param applicationId The unique ID of the application
     * @param applicantNric The NRIC of the applicant
     * @param projectName   The name of the project
     * @param flatType      The type of flat applied for
     */
    public Application(String applicationId, String applicantNric, String projectName, FlatType flatType) {
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.flatBooking = null;
        this.withdrawn = false;
    }

    /**
     * Gets the ID of this application.
     * 
     * @return The application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the NRIC of the applicant.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the name of the project applied for.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the type of flat applied for.
     * 
     * @return The flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Gets the status of this application.
     * 
     * @return The application status
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this application.
     * 
     * @param status The new application status
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    /**
     * Gets the date and time this application was submitted.
     * 
     * @return The application date
     */
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    /**
     * Gets the flat booking associated with this application.
     * 
     * @return The flat booking, or null if no booking
     */
    public FlatBooking getFlatBooking() {
        return flatBooking;
    }

    /**
     * Sets the flat booking associated with this application.
     * 
     * @param flatBooking The new flat booking
     */
    public void setFlatBooking(FlatBooking flatBooking) {
        this.flatBooking = flatBooking;
    }

    /**
     * Checks if this application has a flat booking.
     * 
     * @return true if the application has a booking, false otherwise
     */
    public boolean hasBooking() {
        return flatBooking != null;
    }

    /**
     * Gets if application is withdrawn
     * 
     * @return The application withdrawn
     */
    public Boolean getWithdrawn() {
        return withdrawn;
    }

    /**
     * Sets the application to withdrawn
     * 
     * 
     */
    public void setWithdrawn() {
        this.withdrawn = true;
    }
}