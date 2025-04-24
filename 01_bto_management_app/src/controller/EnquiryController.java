package controller;

import data.EnquiryDB;
import data.ProjectDB;
import data.UserDB;
import entity.Enquiry;
import entity.Project;
import entity.User;
import entity.enums.UserRole;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing enquiries about BTO projects.
 */
public class EnquiryController {
    private EnquiryDB enquiryDB;
    private ProjectDB projectDB;
    private UserDB userDB;

    /**
     * Constructs a new EnquiryController with references to all necessary
     * databases.
     * 
     * @param enquiryDB The enquiry database
     * @param projectDB The project database
     * @param userDB    The user database
     */
    public EnquiryController(EnquiryDB enquiryDB, ProjectDB projectDB, UserDB userDB) {
        this.enquiryDB = enquiryDB;
        this.projectDB = projectDB;
        this.userDB = userDB;
    }

    /**
     * Creates a new enquiry about a project.
     * 
     * @param applicantNric The NRIC of the applicant
     * @param projectName   The name of the project
     * @param enquiryText   The text of the enquiry
     * @return The enquiry ID if successful, null otherwise
     */
    public String createEnquiry(String applicantNric, String projectName, String enquiryText) {
        // Check if applicant and project are valid
        User user = userDB.getUser(applicantNric);
        Project project = projectDB.getProject(projectName);

        if (user == null || project == null || enquiryText == null || enquiryText.trim().isEmpty()) {
            return null;
        }

        // Create and save the enquiry
        String enquiryId = UUID.randomUUID().toString();
        Enquiry enquiry = new Enquiry(enquiryId, applicantNric, projectName, enquiryText);

        if (enquiryDB.addEnquiry(enquiry)) {
            return enquiryId;
        }

        return null;
    }

    /**
     * Gets an enquiry by ID.
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry, or null if not found
     */
    public Enquiry getEnquiry(String enquiryId) {
        return enquiryDB.getEnquiry(enquiryId);
    }

    /**
     * Gets all enquiries for a specific applicant.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return A list of enquiries from the applicant
     */
    public List<Enquiry> getEnquiriesByApplicant(String applicantNric) {
        return enquiryDB.getEnquiriesByApplicant(applicantNric);
    }

    /**
     * Gets all enquiries for a specific project.
     * 
     * @param projectName The name of the project
     * @return A list of enquiries about the project
     */
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        return enquiryDB.getEnquiriesByProject(projectName);
    }

    /**
     * Gets all answered or unanswered enquiries.
     * 
     * @param answered Whether to filter for answered enquiries
     * @return A list of answered or unanswered enquiries
     */
    public List<Enquiry> getEnquiriesByAnswered(boolean answered) {
        return enquiryDB.getEnquiriesByAnswered(answered);
    }

    /**
     * Gets all enquiries in the system.
     * 
     * @return A list of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return enquiryDB.getAllEnquiries();
    }

    /**
     * Updates the text of an existing enquiry.
     * 
     * @param enquiryId   The ID of the enquiry
     * @param enquiryText The new text for the enquiry
     * @return true if successful, false otherwise
     */
    public boolean updateEnquiry(String enquiryId, String enquiryText) {
        Enquiry enquiry = enquiryDB.getEnquiry(enquiryId);
        if (enquiry == null || enquiry.isAnswered() || enquiryText == null || enquiryText.trim().isEmpty()) {
            return false;
        }

        enquiry.setEnquiryText(enquiryText);
        return enquiryDB.updateEnquiry(enquiry);
    }

    /**
     * Deletes an enquiry from the system.
     * 
     * @param enquiryId The ID of the enquiry to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEnquiry(String enquiryId) {
        Enquiry enquiry = enquiryDB.getEnquiry(enquiryId);
        if (enquiry == null) {
            return false;
        }

        return enquiryDB.deleteEnquiry(enquiryId);
    }

    /**
     * Answers an enquiry.
     * 
     * @param enquiryId    The ID of the enquiry
     * @param userNric     The NRIC of the user providing the answer
     * @param responseText The text of the response
     * @return true if successful, false otherwise
     */
    public boolean answerEnquiry(String enquiryId, String userNric, String responseText) {
        // Check if enquiry and user are valid
        Enquiry enquiry = enquiryDB.getEnquiry(enquiryId);
        User user = userDB.getUser(userNric);

        if (enquiry == null || user == null || responseText == null || responseText.trim().isEmpty()) {
            return false;
        }

        // Check if user is authorized to answer
        if (user.getRole() == UserRole.APPLICANT) {
            return false;
        }

        // If user is an officer, check if they handle the project
        if (user.getRole() == UserRole.HDB_OFFICER) {
            if (!(user instanceof entity.HDBOfficer)) {
                return false;
            }

            entity.HDBOfficer officer = (entity.HDBOfficer) user;
            if (!enquiry.getProjectName().equals(officer.getHandlingProjectName())) {
                return false;
            }
        }

        // Update the enquiry with the response
        enquiry.setResponse(responseText);
        return enquiryDB.updateEnquiry(enquiry);
    }
}