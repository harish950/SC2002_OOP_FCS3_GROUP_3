package data;

import entity.Enquiry;

import java.io.*;
import java.util.*;

/**
 * Data access class for Enquiry objects.
 * Handles storage and retrieval of enquiries.
 */
public class EnquiryDB {
    private static final String ENQUIRY_DATA_FILE = "enquiries.dat";
    private Map<String, Enquiry> enquiries;

    /**
     * Constructs a new EnquiryDB and loads existing data if available.
     */
    public EnquiryDB() {
        enquiries = new HashMap<>();
        loadData();
    }

    /**
     * Loads enquiry data from file.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ENQUIRY_DATA_FILE))) {
            enquiries = (Map<String, Enquiry>) ois.readObject();
        } catch (FileNotFoundException e) {
            // System.out.println("Enquiry data file not found. Starting with empty enquiry
            // database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading enquiry data: " + e.getMessage());
        }
    }

    /**
     * Saves enquiry data to file.
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ENQUIRY_DATA_FILE))) {
            oos.writeObject(enquiries);
        } catch (IOException e) {
            System.out.println("Error saving enquiry data: " + e.getMessage());
        }
    }

    /**
     * Adds a new enquiry to the database.
     * 
     * @param enquiry The enquiry to add
     * @return true if the enquiry was added, false if enquiry ID already exists
     */
    public boolean addEnquiry(Enquiry enquiry) {
        if (enquiries.containsKey(enquiry.getEnquiryId())) {
            return false;
        }
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        saveData();
        return true;
    }

    /**
     * Updates an existing enquiry in the database.
     * 
     * @param enquiry The enquiry to update
     * @return true if the enquiry was updated, false if enquiry ID not found
     */
    public boolean updateEnquiry(Enquiry enquiry) {
        if (!enquiries.containsKey(enquiry.getEnquiryId())) {
            return false;
        }
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        saveData();
        return true;
    }

    /**
     * Deletes an enquiry from the database.
     * 
     * @param enquiryId The ID of the enquiry to delete
     * @return true if the enquiry was deleted, false if enquiry ID not found
     */
    public boolean deleteEnquiry(String enquiryId) {
        if (!enquiries.containsKey(enquiryId)) {
            return false;
        }
        enquiries.remove(enquiryId);
        saveData();
        return true;
    }

    /**
     * Gets an enquiry by ID.
     * 
     * @param enquiryId The ID of the enquiry to get
     * @return The enquiry, or null if not found
     */
    public Enquiry getEnquiry(String enquiryId) {
        return enquiries.get(enquiryId);
    }

    /**
     * Gets all enquiries for a specific applicant.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return A list of enquiries from the applicant
     */
    public List<Enquiry> getEnquiriesByApplicant(String applicantNric) {
        List<Enquiry> applicantEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getApplicantNric().equals(applicantNric)) {
                applicantEnquiries.add(enquiry);
            }
        }
        return applicantEnquiries;
    }

    /**
     * Gets all enquiries for a specific project.
     * 
     * @param projectName The name of the project
     * @return A list of enquiries about the project
     */
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        List<Enquiry> projectEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProjectName().equals(projectName)) {
                projectEnquiries.add(enquiry);
            }
        }
        return projectEnquiries;
    }

    /**
     * Gets all answered or unanswered enquiries.
     * 
     * @param answered Whether to filter for answered enquiries
     * @return A list of answered or unanswered enquiries
     */
    public List<Enquiry> getEnquiriesByAnswered(boolean answered) {
        List<Enquiry> filteredEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.isAnswered() == answered) {
                filteredEnquiries.add(enquiry);
            }
        }
        return filteredEnquiries;
    }

    /**
     * Gets all enquiries in the database.
     * 
     * @return A list of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries.values());
    }
}