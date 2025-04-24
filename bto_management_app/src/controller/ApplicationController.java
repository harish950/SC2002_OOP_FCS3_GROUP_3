package controller;

import data.ApplicationDB;
import data.ProjectDB;
import data.UserDB;
import entity.*;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import entity.enums.MaritalStatus;
import entity.enums.UserRole;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing BTO applications and flat bookings.
 */
public class ApplicationController {
    private ApplicationDB applicationDB;
    private ProjectDB projectDB;
    private UserDB userDB;

    /**
     * Constructs a new ApplicationController with references to all necessary
     * databases.
     * 
     * @param applicationDB The application database
     * @param projectDB     The project database
     * @param userDB        The user database
     */
    public ApplicationController(ApplicationDB applicationDB, ProjectDB projectDB, UserDB userDB) {
        this.applicationDB = applicationDB;
        this.projectDB = projectDB;
        this.userDB = userDB;
    }

    /**
     * Creates a new application for a project.
     * 
     * @param applicantNric The NRIC of the applicant
     * @param projectName   The name of the project
     * @param flatType      The flat type applied for
     * @return The application ID if successful, null otherwise
     */
    public String createApplication(String applicantNric, String projectName, FlatType flatType) {
        // Check if applicant, project, and flat type are valid
        User user = userDB.getUser(applicantNric);
        Project project = projectDB.getProject(projectName);

        if (user == null || !(user instanceof Applicant) || project == null) {
            return null;
        }

        // Check if applicant already has an active application
        if (applicationDB.hasActiveApplication(applicantNric)) {
            return null;
        }

        // Check if applicant meets the requirements for the flat type
        if (!isApplicantEligibleForFlatType(user, flatType)) {
            return null;
        }

        // Check if the project has available units of this flat type
        if (!project.getFlatTypeUnits().containsKey(flatType) ||
                project.getFlatTypeUnits().get(flatType) <= 0) {
            return null;
        }

        // Create and save the application
        String applicationId = UUID.randomUUID().toString();
        Application application = new Application(applicationId, applicantNric, projectName, flatType);

        if (applicationDB.addApplication(application)) {
            // Update applicant's current application
            Applicant applicant = (Applicant) user;
            applicant.setCurrentApplicationId(applicationId);
            userDB.updateUser(applicant);

            return applicationId;
        }

        return null;
    }

    /**
     * Checks if an applicant is eligible for a specific flat type.
     * 
     * @param user     The applicant
     * @param flatType The flat type
     * @return true if eligible, false otherwise
     */
    private boolean isApplicantEligibleForFlatType(User user, FlatType flatType) {
        if (user.getMaritalStatus() == MaritalStatus.MARRIED && user.getAge() >= 21) {
            // Married applicants 21 and above can apply for any flat type
            return true;
        } else if (user.getMaritalStatus() == MaritalStatus.SINGLE && user.getAge() >= 35) {
            // Single applicants 35 and above can only apply for 2-Room
            return flatType == FlatType.TWO_ROOM;
        }

        return false;
    }

    /**
     * Gets an application by ID.
     * 
     * @param applicationId The ID of the application
     * @return The application, or null if not found
     */
    public Application getApplication(String applicationId) {
        return applicationDB.getApplication(applicationId);
    }

    /**
     * Gets the current application for an applicant.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return The application, or null if none found
     */
    public Application getCurrentApplication(String applicantNric) {
        return applicationDB.getCurrentApplication(applicantNric);
    }

    /**
     * Gets all applications for a project.
     * 
     * @param projectName The name of the project
     * @return A list of applications for the project
     */
    public List<Application> getApplicationsByProject(String projectName) {
        return applicationDB.getApplicationsByProject(projectName);
    }

    /**
     * Gets all applications with a specific status for a project.
     * 
     * @param projectName The name of the project
     * @param status      The status to filter by
     * @return A list of applications with the specified status
     */
    public List<Application> getApplicationsByProjectAndStatus(String projectName, ApplicationStatus status) {
        List<Application> applications = applicationDB.getApplicationsByProject(projectName);
        applications.removeIf(app -> app.getStatus() != status);
        return applications;
    }

    /**
     * Approves an application.
     * 
     * @param applicationId The ID of the application to approve
     * @return true if successful, false otherwise
     */
    public boolean approveApplication(String applicationId) {
        Application application = applicationDB.getApplication(applicationId);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        // Update application status
        application.setStatus(ApplicationStatus.SUCCESSFUL);
        return applicationDB.updateApplication(application);
    }

    /**
     * Rejects an application.
     * 
     * @param applicationId The ID of the application to reject
     * @return true if successful, false otherwise
     */
    public boolean rejectApplication(String applicationId) {
        Application application = applicationDB.getApplication(applicationId);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        // Update application status
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        applicationDB.updateApplication(application);

        // Update applicant's current application
        User user = userDB.getUser(application.getApplicantNric());
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            applicant.clearApplication();
            userDB.updateUser(applicant);
        }

        return true;
    }

    /**
     * Books a flat for a successful application.
     * 
     * @param applicationId The ID of the application
     * @param officerNric   The NRIC of the officer processing the booking
     * @return The booking ID if successful, null otherwise
     */
    public String bookFlat(String applicationId, String officerNric) {
        // Check if application and officer are valid
        Application application = applicationDB.getApplication(applicationId);
        User officer = userDB.getUser(officerNric);

        if (application == null || officer == null || officer.getRole() != UserRole.HDB_OFFICER) {
            return null;
        }

        // Check if application is successful and not already booked
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL || application.hasBooking()) {
            return null;
        }

        // Check if the officer is handling the project
        HDBOfficer hdbOfficer = (HDBOfficer) officer;
        if (!application.getProjectName().equals(hdbOfficer.getHandlingProjectName())) {
            return null;
        }

        // Create and save the booking
        String bookingId = UUID.randomUUID().toString();
        FlatBooking booking = new FlatBooking(
                bookingId,
                applicationId,
                application.getApplicantNric(),
                application.getProjectName(),
                application.getFlatType(),
                officerNric);

        if (applicationDB.addBooking(booking)) {
            // Decrement flat type units in project
            Project project = projectDB.getProject(application.getProjectName());
            if (project != null) {
                project.decrementFlatTypeUnits(application.getFlatType());
                projectDB.updateProject(project);
            }

            return bookingId;
        }

        return null;
    }

    /**
     * Gets all bookings for a project.
     * 
     * @param projectName The name of the project
     * @return A list of bookings for the project
     */
    public List<FlatBooking> getBookingsByProject(String projectName) {
        return applicationDB.getBookingsByProject(projectName);
    }

    /**
     * Requests withdrawal of an application.
     * 
     * @param applicationId The ID of the application
     * @return true if successful, false otherwise
     */
    public boolean requestWithdrawal(String applicationId) {
        Application application = applicationDB.getApplication(applicationId);
        if (application == null) {
            return false;
        }
        application.setWithdrawn();
        return true;
    }

    /**
     * Approves withdrawal of an application.
     * 
     * @param applicationId The ID of the application
     * @return true if successful, false otherwise
     */
    public boolean approveWithdrawal(String applicationId) {
        Application application = applicationDB.getApplication(applicationId);
        if (application == null) {
            return false;
        }

        // Update project if application was successful or booked
        if (application.getStatus() == ApplicationStatus.SUCCESSFUL ||
                application.getStatus() == ApplicationStatus.BOOKED) {
            Project project = projectDB.getProject(application.getProjectName());
            if (project != null) {
                project.incrementFlatTypeUnits(application.getFlatType());
                projectDB.updateProject(project);
            }
        }

        // Clear applicant's current application
        User user = userDB.getUser(application.getApplicantNric());
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            applicant.clearApplication();
            userDB.updateUser(applicant);
        }

        // Remove the application
        return applicationDB.removeApplication(applicationId);
    }

    /**
     * Generates a receipt for a flat booking.
     * 
     * @param bookingId The ID of the booking
     * @return A string containing the receipt, or null if booking not found
     */
    public String generateReceipt(String bookingId) {
        FlatBooking booking = applicationDB.getBooking(bookingId);
        if (booking == null) {
            return null;
        }

        User applicant = userDB.getUser(booking.getApplicantNric());
        if (applicant == null) {
            return null;
        }

        Project project = projectDB.getProject(booking.getProjectName());
        if (project == null) {
            return null;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("=============== FLAT BOOKING RECEIPT ===============\n");
        receipt.append("Booking ID: ").append(booking.getBookingId()).append("\n");
        receipt.append("Date: ").append(booking.getBookingDate()).append("\n\n");
        receipt.append("Applicant Information:\n");
        receipt.append("Name: ").append(applicant.getNric()).append("\n");
        receipt.append("Age: ").append(applicant.getAge()).append("\n");
        receipt.append("Marital Status: ").append(applicant.getMaritalStatus().getStatus()).append("\n\n");
        receipt.append("Project Information:\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(booking.getFlatType().getDescription()).append("\n\n");
        receipt.append("Officer NRIC: ").append(booking.getOfficerNric()).append("\n");
        receipt.append("=================================================\n");

        return receipt.toString();
    }

    /**
     * Generates a report of bookings filtered by various criteria.
     * 
     * @param projectName   The name of the project (optional)
     * @param flatType      The type of flat (optional)
     * @param maritalStatus The marital status to filter by (optional)
     * @param minAge        The minimum age to filter by (optional)
     * @param maxAge        The maximum age to filter by (optional)
     * @return A list of bookings matching the criteria
     */
    public List<FlatBooking> generateBookingReport(String projectName, FlatType flatType,
            MaritalStatus maritalStatus, Integer minAge, Integer maxAge) {
        return applicationDB.generateBookingReport(projectName, flatType, maritalStatus, minAge, maxAge);
    }
}