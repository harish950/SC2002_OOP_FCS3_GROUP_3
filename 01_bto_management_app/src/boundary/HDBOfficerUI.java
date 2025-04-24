// HDBOfficerUI.java
package boundary;

import controller.*;
import entity.*;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
// import entity.enums.MaritalStatus;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for HDB officer functionality.
 * Handles all interactions for officers managing projects, applications,
 * and responding to enquiries.
 */
public class HDBOfficerUI {
    private Scanner scanner;
    private User currentUser;
    private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;
    private ProjectUI projectUI;
    private LoginUI loginUI;

    /**
     * Constructs a new HDBOfficerUI with references to necessary components.
     * 
     * @param scanner               The scanner for user input
     * @param userController        The user controller
     * @param projectController     The project controller
     * @param applicationController The application controller
     * @param enquiryController     The enquiry controller
     */
    public HDBOfficerUI(Scanner scanner, UserController userController,
            ProjectController projectController,
            ApplicationController applicationController,
            EnquiryController enquiryController, LoginUI loginUI) {
        this.scanner = scanner;
        this.userController = userController;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.projectUI = new ProjectUI(scanner, projectController);
        this.loginUI = loginUI;

    }

    /**
     * Sets the current user for this UI.
     * 
     * @param user The current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Displays the main menu for HDB officers and handles user choices.
     * Provides the core navigation options for officer functionality.
     */
    public void displayMainMenu() {
        if (currentUser == null) {
            System.out.println("Error: No user logged in.");
            return;
        }

        boolean exit = false;

        while (!exit) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|     HDB OFFICER MAIN MENU   |");
            System.out.println("+-----------------------------+");
            System.out.println("| 1. Register to Join a Project Team");
            System.out.println("| 2. View Registration Status");
            System.out.println("| 3. View Handling Project");
            System.out.println("| 4. Flat Selection Management");
            System.out.println("| 5. View and Reply to Enquiries");
            System.out.println("| 6. Change Password");
            System.out.println("| 7. Logout");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        registerForProject();
                        break;
                    case 2:
                        viewRegistrationStatus();
                        break;
                    case 3:
                        viewHandlingProject();
                        break;
                    case 4:
                        manageFlatSelection();
                        break;
                    case 5:
                        viewAndReplyToEnquiries();
                        break;
                    case 6:
                        loginUI.changePassword();
                        break;
                    case 7:
                        exit = true;
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Allows the officer to register to join a project team.
     * Checks eligibility and displays available projects.
     */
    private void registerForProject() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           REGISTER FOR PROJECT                ");
        System.out.println("+-----------------------------------------------");

        // Check if officer is already handling a project
        HDBOfficer officer = (HDBOfficer) currentUser;
        if (officer.isHandlingProject()) {
            System.out.println("You are already handling project: " + officer.getHandlingProjectName());
            System.out.println("   You must complete your current project before registering for a new one.");
            return;
        }

        // Check if officer has applied for any projects as an applicant
        Application currentApplication = applicationController.getCurrentApplication(currentUser.getNric());
        if (currentApplication != null) {
            System.out.println(
                    "You cannot register to handle a project because you have applied for a project as an applicant.");
            System.out.println("   Application for: " + currentApplication.getProjectName());
            System.out.println("   Status: " + currentApplication.getStatus().getStatus());
            return;
        }

        // Display available projects
        List<Project> projects = projectController.getAllProjects();
        List<Project> availableProjects = new java.util.ArrayList<>();

        // Filter projects with available officer slots and not in conflict with
        // applications
        for (Project project : projects) {
            if (project.getAvailableOfficerSlots() > 0 && project.isInApplicationPeriod() &&
                    !isApplicantForProject(currentUser.getNric(), project.getProjectName())) {
                availableProjects.add(project);
            }
        }

        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            System.out.println("   This could be because all projects either:");
            System.out.println("   - Have no available officer slots");
            System.out.println("   - Are not in their application period");
            System.out.println("   - You've already applied for them as an applicant");
            return;
        }

        System.out.println("Available Projects for Registration:");

        // Print table header
        System.out.println("+-----+--------------------+---------------+----------------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-20s | %-13s |%n",
                "No.", "Project Name", "Neighborhood", "Application Period", "Officer Slots");
        System.out.println("+-----+--------------------+---------------+----------------------+---------------+");

        for (int i = 0; i < availableProjects.size(); i++) {
            Project project = availableProjects.get(i);
            String period = project.getApplicationOpeningDate().toString() + " to " +
                    project.getApplicationClosingDate().toString();

            System.out.printf("| %-3d | %-18s | %-13s | %-20s | %-13d |%n",
                    i + 1,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    period,
                    project.getAvailableOfficerSlots());
        }

        System.out.println("+-----+--------------------+---------------+----------------------+---------------+");

        // Let user select a project
        System.out.print("\nEnter the number of the project you wish to register for (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > availableProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = availableProjects.get(projectIndex - 1);

        // Confirm registration
        System.out.println("\nRegistration Summary:");
        System.out.println("   Project: " + selectedProject.getProjectName());
        System.out.println("   Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("   Manager in Charge: " + selectedProject.getManagerInChargeNric());

        System.out.print("\nConfirm registration? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Registration cancelled.");
            return;
        }

        // Register for the project
        String registrationId = projectController.registerOfficerForProject(
                currentUser.getNric(), selectedProject.getProjectName());

        if (registrationId != null) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|      Registration Submitted!            |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Registration ID: " + registrationId);
            System.out.println("Status: PENDING");
            System.out.println("Your registration will be reviewed by the HDB Manager in charge.");
            System.out.println("You can check the status in the 'View Registration Status' menu.");
        } else {
            System.out.println("\nFailed to submit registration. Please try again later.");
        }
    }

    /**
     * Displays the officer's registration status.
     * Shows current project assignment if approved.
     */
    private void viewRegistrationStatus() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           REGISTRATION STATUS                ");
        System.out.println("+-----------------------------------------------");

        HDBOfficer officer = (HDBOfficer) currentUser;
        if (officer.isHandlingProject()) {
            String projectName = officer.getHandlingProjectName();
            Project project = projectController.getProject(projectName);

            if (project == null) {
                System.out.println("Error: Project information not found.");
                return;
            }

            System.out.println("+-----------------------------------------------");
            System.out.println("|  Registration Status:   APPROVED             ");
            System.out.println("+-----------------------------------------------");
            System.out.println("|  Project: " + project.getProjectName());
            System.out.println("|  Neighborhood: " + project.getNeighborhood());
            System.out.println("|  Application Period: " + project.getApplicationOpeningDate() +
                    " to " + project.getApplicationClosingDate());
            System.out.println("|  Manager In Charge: " + project.getManagerInChargeNric());

            // Show remaining units
            System.out.println("|");
            System.out.println("|  Remaining Units:");
            for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
                System.out.println("|  - " + flatType.getDescription() + ": " +
                        project.getFlatTypeUnits().get(flatType));
            }
            System.out.println("+-----------------------------------------------");
        } else {
            System.out.println("+-----------------------------------------------");
            System.out.println("|  Registration Status:   NOT REGISTERED       ");
            System.out.println("+-----------------------------------------------");
            System.out.println("You are not currently assigned to any project.");
            System.out.println("You can register to join a project from the main menu.");
        }
    }

    /**
     * Displays details of the project the officer is handling.
     * Shows applications and project information.
     */
    private void viewHandlingProject() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           HANDLING PROJECT                   ");
        System.out.println("+-----------------------------------------------");

        HDBOfficer officer = (HDBOfficer) currentUser;
        if (!officer.isHandlingProject()) {
            System.out.println("You are not currently handling any project.");
            System.out.println("   You can register to join a project from the main menu.");
            return;
        }

        String projectName = officer.getHandlingProjectName();
        Project project = projectController.getProject(projectName);

        if (project == null) {
            System.out.println("Error: Project not found.");
            return;
        }

        System.out.println("+-----------------------------------------------");
        System.out.println("|  Project Details                             ");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Project Name: " + project.getProjectName());
        System.out.println("|  Neighborhood: " + project.getNeighborhood());
        System.out.println("|  Application Period: " + project.getApplicationOpeningDate() +
                " to " + project.getApplicationClosingDate());
        System.out.println("|  Manager In Charge: " + project.getManagerInChargeNric());
        System.out.println("|  Visibility: " + (project.isVisible() ? "ON" : "OFF"));

        // Show remaining units
        System.out.println("|");
        System.out.println("|  Remaining Units:");
        for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println("|  - " + flatType.getDescription() + ": " +
                    project.getFlatTypeUnits().get(flatType));
        }
        System.out.println("+-----------------------------------------------");

        // Get all applications for this project
        List<Application> applications = applicationController.getApplicationsByProject(projectName);

        if (applications.isEmpty()) {
            System.out.println("\nNo applications found for this project.");
            return;
        }

        // Count applications by status
        int pendingCount = 0;
        int successfulCount = 0;
        int unsuccessfulCount = 0;
        int bookedCount = 0;

        for (Application app : applications) {
            switch (app.getStatus()) {
                case PENDING:
                    pendingCount++;
                    break;
                case SUCCESSFUL:
                    successfulCount++;
                    break;
                case UNSUCCESSFUL:
                    unsuccessfulCount++;
                    break;
                case BOOKED:
                    bookedCount++;
                    break;
            }
        }

        System.out.println("\n   Application Statistics:");
        System.out.println("+----------------+-----------+");
        System.out.println("| Status         | Count     |");
        System.out.println("+----------------+-----------+");
        System.out.printf("| Pending        | %-9d |%n", pendingCount);
        System.out.printf("| Successful     | %-9d |%n", successfulCount);
        System.out.printf("| Unsuccessful   | %-9d |%n", unsuccessfulCount);
        System.out.printf("| Booked         | %-9d |%n", bookedCount);
        System.out.println("+----------------+-----------+");
        System.out.printf("| Total          | %-9d |%n", applications.size());
        System.out.println("+----------------+-----------+");

        // Display all applications
        System.out.println("\nAll Applications:");

        // Print table header
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s |%n",
                "No.", "Application ID", "Applicant NRIC", "Flat Type", "Status");
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Print application details
        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            System.out.printf("| %-3d | %-18s | %-13s | %-13s | %-13s |%n",
                    i + 1,
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getFlatType().getDescription(),
                    application.getStatus().getStatus());
        }

        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Option to view application details
        System.out.print("\nEnter the number of an application to view details (0 to return): ");
        int appIndex;
        try {
            appIndex = Integer.parseInt(scanner.nextLine().trim());
            if (appIndex == 0) {
                return;
            }
            if (appIndex < 1 || appIndex > applications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Application selectedApplication = applications.get(appIndex - 1);
        User applicant = userController.getUser(selectedApplication.getApplicantNric());

        if (applicant == null) {
            System.out.println("Error: Applicant information not found.");
            return;
        }

        // Display application details
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|  Application Details                         ");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Application ID: " + selectedApplication.getApplicationId());
        System.out.println("|  Applicant NRIC: " + applicant.getNric());
        System.out.println("|  Age: " + applicant.getAge());
        System.out.println("|  Marital Status: " + applicant.getMaritalStatus().getStatus());
        System.out.println("|  Flat Type: " + selectedApplication.getFlatType().getDescription());
        System.out.println("|  Status: " + selectedApplication.getStatus().getStatus());
        System.out.println("|  Application Date: " + selectedApplication.getApplicationDate());

        if (selectedApplication.hasBooking()) {
            FlatBooking booking = selectedApplication.getFlatBooking();
            System.out.println("|");
            System.out.println("|  Booking Information:");
            System.out.println("|  - Booking ID: " + booking.getBookingId());
            System.out.println("|  - Booking Date: " + booking.getBookingDate());
            System.out.println("|  - Officer NRIC: " + booking.getOfficerNric());
        }

        System.out.println("+-----------------------------------------------");
    }

    /**
     * Allows the officer to manage flat selection for successful applicants.
     * Processes bookings and generates receipts.
     */
    private void manageFlatSelection() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|         FLAT SELECTION MANAGEMENT            ");
        System.out.println("+-----------------------------------------------");

        HDBOfficer officer = (HDBOfficer) currentUser;
        if (!officer.isHandlingProject()) {
            System.out.println("You are not currently handling any project.");
            System.out.println("   You can register to join a project from the main menu.");
            return;
        }

        String projectName = officer.getHandlingProjectName();
        Project project = projectController.getProject(projectName);

        if (project == null) {
            System.out.println("Error: Project not found.");
            return;
        }

        // Check if there are any units available
        boolean hasAvailableUnits = false;
        for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
            if (project.getFlatTypeUnits().get(flatType) > 0) {
                hasAvailableUnits = true;
                break;
            }
        }

        if (!hasAvailableUnits) {
            System.out.println("There are no available units in this project.");
            System.out.println("   All units have been booked.");
            return;
        }

        // Display successful applications
        List<Application> successfulApplications = applicationController.getApplicationsByProjectAndStatus(
                projectName, ApplicationStatus.SUCCESSFUL);

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications found for this project.");
            System.out.println("   There are no applicants eligible for flat selection.");
            return;
        }

        System.out.println("Successful Applications Pending Booking:");

        // Print table header
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s |%n",
                "No.", "Application ID", "Applicant NRIC", "Flat Type", "Application Date");
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Print application details
        for (int i = 0; i < successfulApplications.size(); i++) {
            Application application = successfulApplications.get(i);
            System.out.printf("| %-3d | %-18s | %-13s | %-13s | %-13s |%n",
                    i + 1,
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getFlatType().getDescription(),
                    application.getApplicationDate().toLocalDate());
        }

        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Let user select an application
        System.out.print("\nEnter the number of the application to process flat booking (0 to cancel): ");
        int applicationIndex;
        try {
            applicationIndex = Integer.parseInt(scanner.nextLine().trim());
            if (applicationIndex == 0) {
                return;
            }
            if (applicationIndex < 1 || applicationIndex > successfulApplications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Application selectedApplication = successfulApplications.get(applicationIndex - 1);

        // Check remaining units for the flat type
        FlatType flatType = selectedApplication.getFlatType();
        int remainingUnits = project.getFlatTypeUnits().getOrDefault(flatType, 0);

        if (remainingUnits <= 0) {
            System.out.println("Error: No more units available for " + flatType.getDescription());
            System.out.println("   Please inform the applicant to apply for a different flat type or project.");
            return;
        }

        // Get applicant details
        User applicant = userController.getUser(selectedApplication.getApplicantNric());
        if (applicant == null) {
            System.out.println("Error: Applicant information not found.");
            return;
        }

        // Display booking summary
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|  Flat Booking Summary                        ");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Application ID: " + selectedApplication.getApplicationId());
        System.out.println("|  Applicant NRIC: " + applicant.getNric());
        System.out.println("|  Age: " + applicant.getAge());
        System.out.println("|  Marital Status: " + applicant.getMaritalStatus().getStatus());
        System.out.println("|  Project: " + project.getProjectName());
        System.out.println("|  Neighborhood: " + project.getNeighborhood());
        System.out.println("|  Flat Type: " + flatType.getDescription());
        System.out.println("|  Units Available: " + remainingUnits);
        System.out.println("+-----------------------------------------------");

        // Confirm booking
        System.out.print("\nConfirm flat booking? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Flat booking cancelled.");
            return;
        }

        // Process flat booking
        String bookingId = applicationController.bookFlat(
                selectedApplication.getApplicationId(), currentUser.getNric());

        if (bookingId != null) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|      Flat Booking Successful!           |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Application Status: Updated to BOOKED");
            System.out.println("Remaining " + flatType.getDescription() + " Units: " +
                    (remainingUnits - 1));

            // Generate and display receipt
            String receipt = applicationController.generateReceipt(bookingId);
            if (receipt != null) {
                System.out.println("\n" + receipt);
                System.out.println("\nBooking receipt generated successfully.");
                System.out.println("Please provide a copy to the applicant.");
            } else {
                System.out.println("\nFailed to generate receipt. Please try again later.");
            }
        } else {
            System.out.println("\nFailed to process flat booking. Please try again later.");
        }
    }

    /**
     * Allows the officer to view and reply to enquiries about their handling
     * project.
     * Lists all enquiries and provides response functionality.
     */
    private void viewAndReplyToEnquiries() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|         VIEW AND REPLY TO ENQUIRIES          ");
        System.out.println("+-----------------------------------------------");

        HDBOfficer officer = (HDBOfficer) currentUser;
        if (!officer.isHandlingProject()) {
            System.out.println("You are not currently handling any project.");
            System.out.println("   You can register to join a project from the main menu.");
            return;
        }

        String projectName = officer.getHandlingProjectName();

        // Get all enquiries for the project
        List<Enquiry> enquiries = enquiryController.getEnquiriesByProject(projectName);
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for project: " + projectName);
            return;
        }

        // Count answered and unanswered enquiries
        int answeredCount = 0;
        int unansweredCount = 0;

        for (Enquiry enquiry : enquiries) {
            if (enquiry.isAnswered()) {
                answeredCount++;
            } else {
                unansweredCount++;
            }
        }

        System.out.println("   Enquiry Statistics:");
        System.out.println("+----------------+-----------+");
        System.out.println("| Status         | Count     |");
        System.out.println("+----------------+-----------+");
        System.out.printf("| Answered       | %-9d |%n", answeredCount);
        System.out.printf("| Unanswered     | %-9d |%n", unansweredCount);
        System.out.println("+----------------+-----------+");
        System.out.printf("| Total          | %-9d |%n", enquiries.size());
        System.out.println("+----------------+-----------+");

        // Display all enquiries
        System.out.println("\nEnquiries for Project: " + projectName);

        // Print table header
        System.out.println("+-----+---------------+------------------------------------+-------------+");
        System.out.printf("| %-3s | %-13s | %-36s | %-11s |%n",
                "No.", "Applicant NRIC", "Enquiry", "Status");
        System.out.println("+-----+---------------+------------------------------------+-------------+");

        // Print enquiry details
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            String enquiryText = enquiry.getEnquiryText();
            if (enquiryText.length() > 36) {
                enquiryText = enquiryText.substring(0, 33) + "...";
            }

            System.out.printf("| %-3d | %-13s | %-36s | %-11s |%n",
                    i + 1,
                    enquiry.getApplicantNric(),
                    enquiryText,
                    enquiry.isAnswered() ? "Answered" : "Pending");
        }

        System.out.println("+-----+---------------+------------------------------------+-------------+");

        // Let user select an enquiry
        System.out.print("\nEnter the number of the enquiry to view or reply (0 to return): ");
        int enquiryIndex;
        try {
            enquiryIndex = Integer.parseInt(scanner.nextLine().trim());
            if (enquiryIndex == 0) {
                return;
            }
            if (enquiryIndex < 1 || enquiryIndex > enquiries.size()) {
                System.out.println("Invalid enquiry number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Enquiry selectedEnquiry = enquiries.get(enquiryIndex - 1);
        User applicant = userController.getUser(selectedEnquiry.getApplicantNric());

        if (applicant == null) {
            System.out.println("Error: Applicant information not found.");
            return;
        }

        // Display enquiry details
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|  Enquiry Details                             ");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("|  Applicant NRIC: " + applicant.getNric());
        System.out.println("|  Submission Time: " + selectedEnquiry.getSubmissionTime());
        System.out.println("|  Status: " + (selectedEnquiry.isAnswered() ? "Answered" : "Pending"));
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Enquiry:");

        // Format multi-line enquiry text
        String[] enquiryLines = selectedEnquiry.getEnquiryText().split("\n");
        for (String line : enquiryLines) {
            System.out.println("|  " + line);
        }

        if (selectedEnquiry.isAnswered()) {
            System.out.println("+-----------------------------------------------");
            System.out.println("|  Response:");

            // Format multi-line response
            String[] responseLines = selectedEnquiry.getResponse().split("\n");
            for (String line : responseLines) {
                System.out.println("|  " + line);
            }
        }

        System.out.println("+-----------------------------------------------");

        if (selectedEnquiry.isAnswered()) {
            // Option to update response
            System.out.print("\nWould you like to update the response? (Y/N): ");
            String updateResponse = scanner.nextLine().trim();

            if (updateResponse.equalsIgnoreCase("Y")) {
                System.out.println("\nEnter new response:");
                System.out.println("(Type your response below, press Enter twice when finished)");

                StringBuilder newResponseBuilder = new StringBuilder();
                String line;
                boolean doubleEnter = false;

                while (!doubleEnter) {
                    line = scanner.nextLine();
                    if (line.isEmpty() && newResponseBuilder.length() > 0) {
                        // Empty line after some content - check for double Enter
                        doubleEnter = true;
                    } else {
                        if (newResponseBuilder.length() > 0) {
                            newResponseBuilder.append("\n");
                        }
                        newResponseBuilder.append(line);
                        doubleEnter = false;
                    }
                }

                String newResponse = newResponseBuilder.toString().trim();

                if (newResponse.isEmpty()) {
                    System.out.println("Response cannot be empty. Operation cancelled.");
                    return;
                }

                boolean answered = enquiryController.answerEnquiry(
                        selectedEnquiry.getEnquiryId(), currentUser.getNric(), newResponse);

                if (answered) {
                    System.out.println("\n+-----------------------------------------+");
                    System.out.println("|      Response Updated!                  |");
                    System.out.println("+-----------------------------------------+");
                    System.out.println("The applicant will be notified of your updated response.");
                } else {
                    System.out.println("\nFailed to update response. Please try again later.");
                }
            }
        } else {
            // Reply to enquiry
            System.out.println("\nEnter your response:");
            System.out.println("(Type your response below, press Enter twice when finished)");

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            boolean doubleEnter = false;

            while (!doubleEnter) {
                line = scanner.nextLine();
                if (line.isEmpty() && responseBuilder.length() > 0) {
                    // Empty line after some content - check for double Enter
                    doubleEnter = true;
                } else {
                    if (responseBuilder.length() > 0) {
                        responseBuilder.append("\n");
                    }
                    responseBuilder.append(line);
                    doubleEnter = false;
                }
            }

            String response = responseBuilder.toString().trim();

            if (response.isEmpty()) {
                System.out.println("Response cannot be empty. Operation cancelled.");
                return;
            }

            boolean answered = enquiryController.answerEnquiry(
                    selectedEnquiry.getEnquiryId(), currentUser.getNric(), response);

            if (answered) {
                System.out.println("\n+-----------------------------------------+");
                System.out.println("|      Response Submitted!                |");
                System.out.println("+-----------------------------------------+");
                System.out.println("The applicant will be notified of your response.");
            } else {
                System.out.println("\nFailed to submit response. Please try again later.");
            }
        }
    }

    /**
     * Checks if the user is an applicant for a specific project.
     * Used to determine eligibility for registration.
     * 
     * @param nric        The NRIC of the user
     * @param projectName The name of the project
     * @return true if the user is an applicant for the project, false otherwise
     */
    private boolean isApplicantForProject(String nric, String projectName) {
        Application application = applicationController.getCurrentApplication(nric);
        return application != null && application.getProjectName().equals(projectName);
    }
}