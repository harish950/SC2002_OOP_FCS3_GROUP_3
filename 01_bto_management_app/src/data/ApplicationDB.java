package data;

import entity.Application;
import entity.FlatBooking;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import entity.enums.MaritalStatus;

import java.io.*;
import java.util.*;

/**
 * Data access class for Application objects.
 * Handles storage and retrieval of applications and flat bookings.
 */
public class ApplicationDB {
    private static final String APPLICATION_DATA_FILE = "applications.dat";
    private static final String BOOKING_DATA_FILE = "bookings.dat";
    private Map<String, Application> applications;
    private Map<String, FlatBooking> bookings;

    /**
     * Constructs a new ApplicationDB and loads existing data if available.
     */
    public ApplicationDB() {
        applications = new HashMap<>();
        bookings = new HashMap<>();
        loadData();
    }

    /**
     * Loads application and booking data from files.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(APPLICATION_DATA_FILE))) {
            applications = (Map<String, Application>) ois.readObject();
        } catch (FileNotFoundException e) {
            // System.out.println("Application data file not found. Starting with empty
            // application database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading application data: " + e.getMessage());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKING_DATA_FILE))) {
            bookings = (Map<String, FlatBooking>) ois.readObject();
        } catch (FileNotFoundException e) {
            // System.out.println("Booking data file not found. Starting with empty booking
            // database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading booking data: " + e.getMessage());
        }
    }

    /**
     * Saves application and booking data to files.
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPLICATION_DATA_FILE))) {
            oos.writeObject(applications);
        } catch (IOException e) {
            System.out.println("Error saving application data: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKING_DATA_FILE))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("Error saving booking data: " + e.getMessage());
        }
    }

    /**
     * Adds a new application to the database.
     * 
     * @param application The application to add
     * @return true if the application was added, false if application ID already
     *         exists
     */
    public boolean addApplication(Application application) {
        if (applications.containsKey(application.getApplicationId())) {
            return false;
        }
        applications.put(application.getApplicationId(), application);
        saveData();
        return true;
    }

    /**
     * Updates an existing application in the database.
     * 
     * @param application The application to update
     * @return true if the application was updated, false if application ID not
     *         found
     */
    public boolean updateApplication(Application application) {
        if (!applications.containsKey(application.getApplicationId())) {
            return false;
        }
        applications.put(application.getApplicationId(), application);
        saveData();
        return true;
    }

    /**
     * Adds a new flat booking to the database.
     * 
     * @param booking The booking to add
     * @return true if the booking was added, false if booking ID already exists
     */
    public boolean addBooking(FlatBooking booking) {
        if (bookings.containsKey(booking.getBookingId())) {
            return false;
        }
        bookings.put(booking.getBookingId(), booking);

        // Update the associated application
        Application application = applications.get(booking.getApplicationId());
        if (application != null) {
            application.setFlatBooking(booking);
            application.setStatus(ApplicationStatus.BOOKED);
            applications.put(application.getApplicationId(), application);
        }

        saveData();
        return true;
    }

    /**
     * Gets an application by ID.
     * 
     * @param applicationId The ID of the application to get
     * @return The application, or null if not found
     */
    public Application getApplication(String applicationId) {
        return applications.get(applicationId);
    }

    /**
     * Gets a booking by ID.
     * 
     * @param bookingId The ID of the booking to get
     * @return The booking, or null if not found
     */
    public FlatBooking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    /**
     * Gets all applications for a specific applicant.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return A list of applications for the applicant
     */
    public List<Application> getApplicationsByApplicant(String applicantNric) {
        List<Application> applicantApplications = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getApplicantNric().equals(applicantNric)) {
                applicantApplications.add(application);
            }
        }
        return applicantApplications;
    }

    /**
     * Gets all applications for a specific project.
     * 
     * @param projectName The name of the project
     * @return A list of applications for the project
     */
    public List<Application> getApplicationsByProject(String projectName) {
        List<Application> projectApplications = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getProjectName().equals(projectName)) {
                projectApplications.add(application);
            }
        }
        return projectApplications;
    }

    /**
     * Gets all applications with a specific status.
     * 
     * @param status The status to filter by
     * @return A list of applications with the specified status
     */
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        List<Application> statusApplications = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getStatus() == status) {
                statusApplications.add(application);
            }
        }
        return statusApplications;
    }

    /**
     * Gets all successful applications for a specific project.
     * 
     * @param projectName The name of the project
     * @return A list of successful applications for the project
     */
    public List<Application> getSuccessfulApplicationsByProject(String projectName) {
        List<Application> successfulApplications = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getProjectName().equals(projectName) &&
                    (application.getStatus() == ApplicationStatus.SUCCESSFUL ||
                            application.getStatus() == ApplicationStatus.BOOKED)) {
                successfulApplications.add(application);
            }
        }
        return successfulApplications;
    }

    /**
     * Gets all bookings for a specific project.
     * 
     * @param projectName The name of the project
     * @return A list of bookings for the project
     */
    public List<FlatBooking> getBookingsByProject(String projectName) {
        List<FlatBooking> projectBookings = new ArrayList<>();
        for (FlatBooking booking : bookings.values()) {
            if (booking.getProjectName().equals(projectName)) {
                projectBookings.add(booking);
            }
        }
        return projectBookings;
    }

    /**
     * Removes an application from the database.
     * 
     * @param applicationId The ID of the application to remove
     * @return true if the application was removed, false if not found
     */
    public boolean removeApplication(String applicationId) {
        if (!applications.containsKey(applicationId)) {
            return false;
        }

        // Remove any associated booking
        Application application = applications.get(applicationId);
        if (application.hasBooking()) {
            bookings.remove(application.getFlatBooking().getBookingId());
        }

        applications.remove(applicationId);
        saveData();
        return true;
    }

    /**
     * Gets the current application for an applicant.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return The current application, or null if none found
     */
    public Application getCurrentApplication(String applicantNric) {
        for (Application application : applications.values()) {
            if (application.getApplicantNric().equals(applicantNric) &&
                    application.getStatus() != ApplicationStatus.UNSUCCESSFUL) {
                return application;
            }
        }
        return null;
    }

    /**
     * Checks if an applicant has a pending or successful application.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return true if the applicant has an active application, false otherwise
     */
    public boolean hasActiveApplication(String applicantNric) {
        for (Application application : applications.values()) {
            if (application.getApplicantNric().equals(applicantNric) &&
                    (application.getStatus() == ApplicationStatus.PENDING ||
                            application.getStatus() == ApplicationStatus.SUCCESSFUL ||
                            application.getStatus() == ApplicationStatus.BOOKED)) {
                return true;
            }
        }
        return false;
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
        List<FlatBooking> reportBookings = new ArrayList<>(bookings.values());

        // Filter by project name
        if (projectName != null && !projectName.isEmpty()) {
            reportBookings.removeIf(booking -> !booking.getProjectName().equals(projectName));
        }

        // Filter by flat type
        if (flatType != null) {
            reportBookings.removeIf(booking -> booking.getFlatType() != flatType);
        }

        // For the remaining filters, we need user information
        UserDB userDB = new UserDB();

        // Filter by marital status
        if (maritalStatus != null) {
            reportBookings.removeIf(booking -> {
                entity.User user = userDB.getUser(booking.getApplicantNric());
                return user == null || user.getMaritalStatus() != maritalStatus;
            });
        }

        // Filter by age range
        if (minAge != null || maxAge != null) {
            reportBookings.removeIf(booking -> {
                entity.User user = userDB.getUser(booking.getApplicantNric());
                if (user == null)
                    return true;
                int age = user.getAge();
                return (minAge != null && age < minAge) || (maxAge != null && age > maxAge);
            });
        }

        return reportBookings;
    }
}
