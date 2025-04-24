package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an enquiry submitted by a user about a project.
 */
public class Enquiry implements Serializable {
    private String enquiryId;
    private String applicantNric;
    private String projectName;
    private String enquiryText;
    private String response;
    private LocalDateTime submissionTime;
    private boolean isAnswered;

    /**
     * Creates a new enquiry with the specified details.
     * 
     * @param enquiryId     The unique ID of the enquiry
     * @param applicantNric The NRIC of the applicant who submitted the enquiry
     * @param projectName   The name of the project the enquiry is about
     * @param enquiryText   The text of the enquiry
     */
    public Enquiry(String enquiryId, String applicantNric, String projectName, String enquiryText) {
        this.enquiryId = enquiryId;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.response = null;
        this.submissionTime = LocalDateTime.now();
        this.isAnswered = false;
    }

    /**
     * Gets the ID of this enquiry.
     * 
     * @return The enquiry ID
     */
    public String getEnquiryId() {
        return enquiryId;
    }

    /**
     * Gets the NRIC of the applicant who submitted this enquiry.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the name of the project this enquiry is about.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the text of this enquiry.
     * 
     * @return The enquiry text
     */
    public String getEnquiryText() {
        return enquiryText;
    }

    /**
     * Sets the text of this enquiry.
     * 
     * @param enquiryText The new enquiry text
     */
    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    /**
     * Gets the response to this enquiry.
     * 
     * @return The response text, or null if not answered
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the response to this enquiry.
     * 
     * @param response The response text
     */
    public void setResponse(String response) {
        this.response = response;
        this.isAnswered = true;
    }

    /**
     * Gets the submission time of this enquiry.
     * 
     * @return The submission time
     */
    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    /**
     * Checks if this enquiry has been answered.
     * 
     * @return true if the enquiry has been answered, false otherwise
     */
    public boolean isAnswered() {
        return isAnswered;
    }
}