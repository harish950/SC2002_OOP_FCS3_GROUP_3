package entity;

import entity.enums.FlatType;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a flat booking in the system.
 */
public class FlatBooking implements Serializable {
    private String bookingId;
    private String applicationId;
    private String applicantNric;
    private String projectName;
    private FlatType flatType;
    private LocalDateTime bookingDate;
    private String officerNric;

    /**
     * Creates a new flat booking with the specified details.
     * 
     * @param bookingId     The unique ID of the booking
     * @param applicationId The ID of the associated application
     * @param applicantNric The NRIC of the applicant
     * @param projectName   The name of the project
     * @param flatType      The type of flat booked
     * @param officerNric   The NRIC of the officer who processed the booking
     */
    public FlatBooking(String bookingId, String applicationId, String applicantNric,
            String projectName, FlatType flatType, String officerNric) {
        this.bookingId = bookingId;
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.flatType = flatType;
        this.bookingDate = LocalDateTime.now();
        this.officerNric = officerNric;
    }

    /**
     * Gets the ID of this booking.
     * 
     * @return The booking ID
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * Gets the ID of the associated application.
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
     * Gets the name of the project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the type of flat booked.
     * 
     * @return The flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Gets the date and time this booking was made.
     * 
     * @return The booking date
     */
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    /**
     * Gets the NRIC of the officer who processed this booking.
     * 
     * @return The officer's NRIC
     */
    public String getOfficerNric() {
        return officerNric;
    }
}