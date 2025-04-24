// ApplicantUI.java
package boundary;

import controller.*;
import entity.*;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import entity.enums.MaritalStatus;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for applicant functionality.
 * Handles all interactions for applicants viewing projects, applying for BTOs,
 * and managing enquiries.
 */
public class ApplicantUI {
    private Scanner scanner;
    private User currentUser;
    // private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;

    private LoginUI loginUI;

    /**
     * Constructs a new ApplicantUI with references to necessary components.
     * 
     * @param scanner               The scanner for user input
     * @param userController        The user controller
     * @param projectController     The project controller
     * @param applicationController The application controller
     * @param enquiryController     The enquiry controller
     */
    public ApplicantUI(Scanner scanner, UserController userController,
            ProjectController projectController,
            ApplicationController applicationController,
            EnquiryController enquiryController,
            LoginUI loginUI) {
        this.scanner = scanner;
        // this.userController = userController;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
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
     * Displays the main menu for applicants and handles user choices.
     * Provides the core navigation options for applicant functionality.
     */
    public void displayMainMenu() {
        if (currentUser == null) {
            System.out.println("Error: No user logged in.");
            return;
        }

        boolean exit = false;
        String filterNeighborhood = null;
        FlatType filterFlatType = null;

        while (!exit) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|     APPLICANT MAIN MENU     |");
            System.out.println("+-----------------------------+");
            System.out.println("1. View All Projects");
            System.out.println("2. Apply for a Project");
            System.out.println("3. View My Application");
            System.out.println("4. Request Application Withdrawal");
            System.out.println("5. Manage Enquiries");
            System.out.println("6. Filter Settings");
            System.out.println("7. Change Password");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        viewAvailableProjects(filterNeighborhood, filterFlatType);
                        break;
                    case 2:
                        applyForProject(filterNeighborhood, filterFlatType);
                        break;
                    case 3:
                        viewMyApplication();
                        break;
                    case 4:
                        requestWithdrawal();
                        break;
                    case 5:
                        manageEnquiries();
                        break;
                    case 6:
                        Object[] filters = updateFilterSettings(filterNeighborhood, filterFlatType);
                        filterNeighborhood = (String) filters[0];
                        filterFlatType = (FlatType) filters[1];
                        break;
                    case 7:
                        loginUI.changePassword();
                        break;
                    case 8:
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
     * Displays available projects based on the applicant's eligibility.
     * Applies filters if set and allows viewing project details.
     * 
     * @param filterNeighborhood The neighborhood filter
     * @param filterFlatType     The flat type filter
     */
    private void viewAvailableProjects(String filterNeighborhood, FlatType filterFlatType) {
        System.out.println("\n+-----------------------------+");
        System.out.println("|     AVAILABLE PROJECTS      |");
        System.out.println("+-----------------------------+");

        boolean isMarried = currentUser.getMaritalStatus() == MaritalStatus.MARRIED;
        List<Project> projects = projectController.getVisibleProjectsByMaritalStatus(isMarried);

        // Apply filters if set
        if (filterNeighborhood != null && !filterNeighborhood.isEmpty()) {
            projects = projectController.filterByNeighborhood(projects, filterNeighborhood);
        }

        if (filterFlatType != null) {
            projects = projectController.filterByFlatType(projects, filterFlatType);
        }

        if (projects.isEmpty()) {
            System.out.println("No projects available for your eligibility criteria and filters.");
            return;
        }

        // Display applied filters if any
        if (filterNeighborhood != null || filterFlatType != null) {
            System.out.println("Applied Filters:");
            if (filterNeighborhood != null) {
                System.out.println("- Neighborhood: " + filterNeighborhood);
            }
            if (filterFlatType != null) {
                System.out.println("- Flat Type: " + filterFlatType.getDescription());
            }
            System.out.println();
        }

        // Print table header
        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s | %-13s | %-13s |%n",
                "No.", "Project Name", "Neighborhood", "2-Room Units", "3-Room Units",
                "Opening Date", "Closing Date");
        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+");

        // Print project details
        int index = 1;

        for (Project project : projects) {
            System.out.printf("| %-3d | %-18s | %-13s | %-13d | %-13d | %-13s | %-13s |%n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0),
                    project.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate());
        }

        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+");

        // Option to view a specific project by number
        System.out.print("\nEnter the number of the project you wish to view (0 to return): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            } else if (selection > 0 && selection <= projects.size()) {
                String projectName = projects.get(selection - 1).getProjectName();
                viewProjectDetails(projectName);
            } else {
                System.out.println("Invalid project number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Displays detailed information about a specific project.
     * Allows creating an enquiry about the project.
     * 
     * @param projectName The name of the project to view
     */
    private void viewProjectDetails(String projectName) {
        Project project = projectController.getProject(projectName);

        if (project == null || (!project.isVisible() &&
                !hasApplicationForProject(currentUser.getNric(), projectName))) {
            System.out.println("Project not found or not available for viewing.");
            return;
        }

        System.out.println("\n+-----------------------------+");
        System.out.println("|       PROJECT DETAILS       |");
        System.out.println("+-----------------------------+");
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                " to " + project.getApplicationClosingDate());
        System.out.println("Manager In Charge: " + project.getManagerInChargeNric());

        System.out.println("\nFlat Types and Units:");
        for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println("- " + flatType.getDescription() + ": " +
                    project.getFlatTypeUnits().get(flatType) + " units");
        }

        // Check if project is currently in application period
        boolean inApplicationPeriod = project.isInApplicationPeriod();
        if (inApplicationPeriod) {
            System.out.println("\nStatus: OPEN for applications");
        } else {
            System.out.println("\nStatus: NOT OPEN for applications");
        }

        // Option to create an enquiry
        System.out.print("\nWould you like to submit an enquiry about this project? (Y/N): ");
        String createEnquiry = scanner.nextLine().trim();

        if (createEnquiry.equalsIgnoreCase("Y")) {
            createEnquiry(projectName);
        }
    }

    /**
     * Allows the applicant to apply for a project.
     * Checks eligibility and displays available projects.
     * 
     * @param filterNeighborhood The neighborhood filter
     * @param filterFlatType     The flat type filter
     */
    private void applyForProject(String filterNeighborhood, FlatType filterFlatType) {
        System.out.println("\n+-----------------------------+");
        System.out.println("|      APPLY FOR PROJECT      |");
        System.out.println("+-----------------------------+");

        // Check if applicant already has an active application
        Application currentApplication = applicationController.getCurrentApplication(currentUser.getNric());
        if (currentApplication != null) {
            System.out.println("You already have an active application for project: " +
                    currentApplication.getProjectName());
            System.out.println("Status: " + currentApplication.getStatus().getStatus());
            return;
        }

        // Check eligibility - age and marital status
        boolean eligible = false;
        if (currentUser.getMaritalStatus() == MaritalStatus.MARRIED && currentUser.getAge() >= 21) {
            eligible = true;
        } else if (currentUser.getMaritalStatus() == MaritalStatus.SINGLE && currentUser.getAge() >= 35) {
            eligible = true;
        }

        if (!eligible) {
            System.out.println("You are not eligible to apply for any BTO projects.");
            if (currentUser.getMaritalStatus() == MaritalStatus.SINGLE) {
                System.out.println("Single applicants must be 35 years old and above.");
            } else {
                System.out.println("Married applicants must be 21 years old and above.");
            }
            return;
        }

        // Display available projects
        boolean isMarried = currentUser.getMaritalStatus() == MaritalStatus.MARRIED;
        List<Project> projects = projectController.getVisibleProjectsByMaritalStatus(isMarried);

        // Apply filters if set
        if (filterNeighborhood != null && !filterNeighborhood.isEmpty()) {
            projects = projectController.filterByNeighborhood(projects, filterNeighborhood);
        }

        if (filterFlatType != null) {
            projects = projectController.filterByFlatType(projects, filterFlatType);
        }

        // Filter only projects that are in application period
        List<Project> openProjects = new java.util.ArrayList<>();
        for (Project project : projects) {
            if (project.isInApplicationPeriod()) {
                openProjects.add(project);
            }
        }

        if (openProjects.isEmpty()) {
            System.out.println("No projects currently open for application.");
            return;
        }

        // Print table header
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s |%n",
                "No.", "Project Name", "Neighborhood", "2-Room Units", "3-Room Units");
        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Print project details
        for (int i = 0; i < openProjects.size(); i++) {
            Project project = openProjects.get(i);
            System.out.printf("| %-3d | %-18s | %-13s | %-13d | %-13d |%n",
                    i + 1,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0),
                    project.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0));
        }

        System.out.println("+-----+--------------------+---------------+---------------+---------------+");

        // Let user select a project
        System.out.print("\nEnter the number of the project you wish to apply for (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > openProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = openProjects.get(projectIndex - 1);

        // Let user select a flat type
        System.out.println("\nSelect flat type:");

        // Display available flat types based on eligibility
        List<FlatType> availableFlatTypes = getEligibleFlatTypes();
        List<FlatType> filteredFlatTypes = new java.util.ArrayList<>();

        for (FlatType flatType : availableFlatTypes) {
            if (selectedProject.getFlatTypeUnits().containsKey(flatType) &&
                    selectedProject.getFlatTypeUnits().get(flatType) > 0) {
                filteredFlatTypes.add(flatType);
            }
        }

        if (filteredFlatTypes.isEmpty()) {
            System.out.println("No flat types available for your eligibility criteria.");
            return;
        }

        for (int i = 0; i < filteredFlatTypes.size(); i++) {
            FlatType flatType = filteredFlatTypes.get(i);
            System.out.println((i + 1) + ". " + flatType.getDescription() +
                    " (" + selectedProject.getFlatTypeUnits().get(flatType) + " units available)");
        }

        System.out.print("\nEnter the number of the flat type (0 to cancel): ");
        int flatTypeIndex;
        try {
            flatTypeIndex = Integer.parseInt(scanner.nextLine().trim());
            if (flatTypeIndex == 0) {
                return;
            }
            if (flatTypeIndex < 1 || flatTypeIndex > filteredFlatTypes.size()) {
                System.out.println("Invalid flat type number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        FlatType selectedFlatType = filteredFlatTypes.get(flatTypeIndex - 1);

        // Confirm application
        System.out.println("\nApplication Summary:");
        System.out.println("Project: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Flat Type: " + selectedFlatType.getDescription());

        System.out.print("\nConfirm application? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Application cancelled.");
            return;
        }

        // Create application
        String applicationId = applicationController.createApplication(
                currentUser.getNric(), selectedProject.getProjectName(), selectedFlatType);

        if (applicationId != null) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|       Application Submitted!           |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Application ID: " + applicationId);
            System.out.println("Status: PENDING");
            System.out.println("\nYour application will be reviewed by the HDB Manager.");
        } else {
            System.out.println("\nFailed to submit application. Please try again later.");
        }
    }

    /**
     * Gets the flat types that the current applicant is eligible for.
     * Based on marital status and age requirements.
     * 
     * @return A list of eligible flat types
     */
    private List<FlatType> getEligibleFlatTypes() {
        List<FlatType> eligibleTypes = new java.util.ArrayList<>();

        if (currentUser.getMaritalStatus() == MaritalStatus.MARRIED && currentUser.getAge() >= 21) {
            // Married applicants 21 and above can apply for any flat type
            eligibleTypes.add(FlatType.TWO_ROOM);
            eligibleTypes.add(FlatType.THREE_ROOM);
        } else if (currentUser.getMaritalStatus() == MaritalStatus.SINGLE && currentUser.getAge() >= 35) {
            // Single applicants 35 and above can only apply for 2-Room
            eligibleTypes.add(FlatType.TWO_ROOM);
        }

        return eligibleTypes;
    }

    /**
     * Displays information about the applicant's current application.
     * Shows booking information if available and allows viewing receipt.
     */
    private void viewMyApplication() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|        MY APPLICATION       |");
        System.out.println("+-----------------------------+");

        Application application = applicationController.getCurrentApplication(currentUser.getNric());
        if (application == null) {
            System.out.println("You don't have any active applications.");
            return;
        }

        // Get project details for more information
        Project project = projectController.getProject(application.getProjectName());
        if (project == null) {
            System.out.println("Error: Project information not found.");
            return;
        }

        System.out.println("+-----------------------------------------------");
        System.out.println("|  Application Details");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  Application ID: " + application.getApplicationId());
        System.out.println("|  Project: " + application.getProjectName());
        System.out.println("|  Neighborhood: " + project.getNeighborhood());
        System.out.println("|  Flat Type: " + application.getFlatType().getDescription());
        System.out.println("|  Status: " + application.getStatus().getStatus());
        System.out.println("|  Application Date: " + application.getApplicationDate());
        System.out.println("+-----------------------------------------------");

        // Display status-specific information and actions
        if (application.getStatus() == ApplicationStatus.PENDING) {
            System.out.println("\nYour application is being reviewed by the HDB Manager.");
            System.out.println("Please check back later for updates.");
        } else if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
            System.out.println("\nCongratulations! Your application has been approved.");
            System.out.println("Please contact an HDB Officer to book your flat.");
        } else if (application.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            System.out.println("\nWe regret to inform you that your application was unsuccessful.");
            System.out.println("You may apply for other BTO projects.");
        }

        if (application.hasBooking()) {
            FlatBooking booking = application.getFlatBooking();
            System.out.println("\n+-----------------------------------------------");
            System.out.println("|  Booking Information");
            System.out.println("+-----------------------------------------------");
            System.out.println("|  Booking ID: " + booking.getBookingId());
            System.out.println("|  Booking Date: " + booking.getBookingDate());
            System.out.println("|  Officer NRIC: " + booking.getOfficerNric());
            System.out.println("+-----------------------------------------------");

            // Option to view receipt
            System.out.print("\nWould you like to view the booking receipt? (Y/N): ");
            String viewReceipt = scanner.nextLine().trim();

            if (viewReceipt.equalsIgnoreCase("Y")) {
                String receipt = applicationController.generateReceipt(booking.getBookingId());
                if (receipt != null) {
                    System.out.println("\n" + receipt);
                } else {
                    System.out.println("Failed to generate receipt.");
                }
            }
        }
    }

    /**
     * Allows the applicant to request withdrawal of their application.
     * Confirms the action and submits the request.
     */
    private void requestWithdrawal() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|  REQUEST APPLICATION WITHDRAWAL  |");
        System.out.println("+-----------------------------+");

        Application application = applicationController.getCurrentApplication(currentUser.getNric());
        if (application == null) {
            System.out.println("You don't have any active applications to withdraw.");
            return;
        }

        System.out.println("Current Application:");
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Project: " + application.getProjectName());
        System.out.println("Flat Type: " + application.getFlatType().getDescription());
        System.out.println("Status: " + application.getStatus().getStatus());

        if (application.hasBooking()) {
            System.out.println("\nWarning: Your application has a confirmed flat booking.");
            System.out.println("Withdrawing will release your booked flat.");
        }

        System.out.print("\nAre you sure you want to request withdrawal? This action cannot be undone. (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = applicationController.requestWithdrawal(application.getApplicationId());

            if (success) {
                System.out.println("\n+-----------------------------------------+");
                System.out.println("|     Withdrawal Request Submitted!     |");
                System.out.println("+-----------------------------------------+");
                System.out.println("Your request will be reviewed by the HDB Manager.");
                System.out.println("You will be notified once the request is processed.");
            } else {
                System.out.println("\nFailed to submit withdrawal request. Please try again later.");
            }
        } else {
            System.out.println("Withdrawal request cancelled.");
        }
    }

    /**
     * Allows the applicant to manage their enquiries.
     * Provides options to view, create, edit, and delete enquiries.
     */
    private void manageEnquiries() {
        boolean back = false;

        while (!back) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|       MANAGE ENQUIRIES      |");
            System.out.println("+-----------------------------+");
            System.out.println("1. View My Enquiries");
            System.out.println("2. Create New Enquiry");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        viewMyEnquiries();
                        break;
                    case 2:
                        createNewEnquiry();
                        break;
                    case 3:
                        editEnquiry();
                        break;
                    case 4:
                        deleteEnquiry();
                        break;
                    case 5:
                        back = true;
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
     * Displays the applicant's enquiries.
     * Shows details and responses for selected enquiries.
     */
    private void viewMyEnquiries() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|        MY ENQUIRIES         |");
        System.out.println("+-----------------------------+");

        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(currentUser.getNric());
        if (enquiries.isEmpty()) {
            System.out.println("You don't have any enquiries.");
            return;
        }

        // Print table header
        System.out.println("+-----+--------------------+------------------------------------+----------+");
        System.out.printf("| %-3s | %-18s | %-36s | %-8s |%n",
                "No.", "Project", "Enquiry", "Answered");
        System.out.println("+-----+--------------------+------------------------------------+----------+");

        // Print enquiry details
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            String enquiryText = enquiry.getEnquiryText();
            if (enquiryText.length() > 36) {
                enquiryText = enquiryText.substring(0, 33) + "...";
            }

            System.out.printf("| %-3d | %-18s | %-36s | %-8s |%n",
                    i + 1,
                    enquiry.getProjectName(),
                    enquiryText,
                    enquiry.isAnswered() ? "Yes" : "No");
        }

        System.out.println("+-----+--------------------+------------------------------------+----------+");

        // Option to view a specific enquiry
        System.out.print("\nEnter the number of the enquiry to view details (0 to return): ");
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

        System.out.println("\n+-----------------------------+");
        System.out.println("|       ENQUIRY DETAILS       |");
        System.out.println("+-----------------------------+");
        System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("Project: " + selectedEnquiry.getProjectName());
        System.out.println("Submission Time: " + selectedEnquiry.getSubmissionTime());
        System.out.println("\nEnquiry: " + selectedEnquiry.getEnquiryText());

        if (selectedEnquiry.isAnswered()) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|          RESPONSE           |");
            System.out.println("+-----------------------------+");
            System.out.println(selectedEnquiry.getResponse());
        } else {
            System.out.println("\nStatus: Awaiting response");
        }
    }

    /**
     * Allows the applicant to create a new enquiry.
     * Selects a project and enters enquiry text.
     */
    private void createNewEnquiry() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|      CREATE NEW ENQUIRY     |");
        System.out.println("+-----------------------------+");

        // Display available projects
        boolean isMarried = currentUser.getMaritalStatus() == MaritalStatus.MARRIED;
        List<Project> projects = projectController.getVisibleProjectsByMaritalStatus(isMarried);

        // Add current application project if not visible
        Application currentApplication = applicationController.getCurrentApplication(currentUser.getNric());
        if (currentApplication != null) {
            String currentProjectName = currentApplication.getProjectName();
            boolean projectExists = false;

            for (Project project : projects) {
                if (project.getProjectName().equals(currentProjectName)) {
                    projectExists = true;
                    break;
                }
            }

            if (!projectExists) {
                Project currentProject = projectController.getProject(currentProjectName);
                if (currentProject != null) {
                    projects.add(currentProject);
                }
            }
        }

        if (projects.isEmpty()) {
            System.out.println("No projects available for enquiry.");
            return;
        }

        // Print table header
        System.out.println("+-----+--------------------+---------------+");
        System.out.printf("| %-3s | %-18s | %-13s |%n",
                "No.", "Project Name", "Neighborhood");
        System.out.println("+-----+--------------------+---------------+");

        // Print project details
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("| %-3d | %-18s | %-13s |%n",
                    i + 1,
                    project.getProjectName(),
                    project.getNeighborhood());
        }

        System.out.println("+-----+--------------------+---------------+");

        // Let user select a project
        System.out.print("\nEnter the number of the project for your enquiry (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > projects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = projects.get(projectIndex - 1);

        // Get enquiry text
        System.out.println("\nEnter your enquiry about " + selectedProject.getProjectName() + ":");
        System.out.println("(Type your question below, press Enter twice when finished)");

        StringBuilder enquiryTextBuilder = new StringBuilder();
        String line;
        boolean doubleEnter = false;

        while (!doubleEnter) {
            line = scanner.nextLine();
            if (line.isEmpty() && enquiryTextBuilder.length() > 0) {
                // Empty line after some content - check for double Enter
                doubleEnter = true;
            } else {
                if (enquiryTextBuilder.length() > 0) {
                    enquiryTextBuilder.append("\n");
                }
                enquiryTextBuilder.append(line);
                doubleEnter = false;
            }
        }

        String enquiryText = enquiryTextBuilder.toString().trim();

        if (enquiryText.isEmpty()) {
            System.out.println("Enquiry cannot be empty. Operation cancelled.");
            return;
        }

        // Create enquiry
        String enquiryId = enquiryController.createEnquiry(
                currentUser.getNric(), selectedProject.getProjectName(), enquiryText);

        if (enquiryId != null) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|        Enquiry Submitted!              |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enquiry ID: " + enquiryId);
            System.out.println("You will be notified when there is a response.");
        } else {
            System.out.println("\nFailed to submit enquiry. Please try again later.");
        }
    }

    /**
     * Allows the applicant to edit an existing enquiry.
     * Only unanswered enquiries can be edited.
     */
    private void editEnquiry() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|         EDIT ENQUIRY        |");
        System.out.println("+-----------------------------+");

        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(currentUser.getNric());

        // Filter for unanswered enquiries only
        List<Enquiry> unansweredEnquiries = new java.util.ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (!enquiry.isAnswered()) {
                unansweredEnquiries.add(enquiry);
            }
        }

        if (unansweredEnquiries.isEmpty()) {
            System.out.println("You don't have any unanswered enquiries to edit.");
            System.out.println("Note: You cannot edit enquiries that have already been answered.");
            return;
        }

        // Print table header
        System.out.println("+-----+--------------------+------------------------------------+");
        System.out.printf("| %-3s | %-18s | %-36s |%n",
                "No.", "Project", "Enquiry");
        System.out.println("+-----+--------------------+------------------------------------+");

        // Print enquiry details
        for (int i = 0; i < unansweredEnquiries.size(); i++) {
            Enquiry enquiry = unansweredEnquiries.get(i);
            String enquiryText = enquiry.getEnquiryText();
            if (enquiryText.length() > 36) {
                enquiryText = enquiryText.substring(0, 33) + "...";
            }

            System.out.printf("| %-3d | %-18s | %-36s |%n",
                    i + 1,
                    enquiry.getProjectName(),
                    enquiryText);
        }

        System.out.println("+-----+--------------------+------------------------------------+");

        // Let user select an enquiry
        System.out.print("\nEnter the number of the enquiry to edit (0 to cancel): ");
        int enquiryIndex;
        try {
            enquiryIndex = Integer.parseInt(scanner.nextLine().trim());
            if (enquiryIndex == 0) {
                return;
            }
            if (enquiryIndex < 1 || enquiryIndex > unansweredEnquiries.size()) {
                System.out.println("Invalid enquiry number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Enquiry selectedEnquiry = unansweredEnquiries.get(enquiryIndex - 1);

        // Display current text and get new text
        System.out.println("\nCurrent Enquiry: " + selectedEnquiry.getEnquiryText());
        System.out.println("\nEnter new enquiry text:");
        System.out.println("(Type your question below, press Enter twice when finished)");

        StringBuilder newEnquiryTextBuilder = new StringBuilder();
        String line;
        boolean doubleEnter = false;

        while (!doubleEnter) {
            line = scanner.nextLine();
            if (line.isEmpty() && newEnquiryTextBuilder.length() > 0) {
                // Empty line after some content - check for double Enter
                doubleEnter = true;
            } else {
                if (newEnquiryTextBuilder.length() > 0) {
                    newEnquiryTextBuilder.append("\n");
                }
                newEnquiryTextBuilder.append(line);
                doubleEnter = false;
            }
        }

        String newEnquiryText = newEnquiryTextBuilder.toString().trim();

        if (newEnquiryText.isEmpty()) {
            System.out.println("Enquiry cannot be empty. Operation cancelled.");
            return;
        }

        // Update enquiry
        boolean updated = enquiryController.updateEnquiry(
                selectedEnquiry.getEnquiryId(), newEnquiryText);

        if (updated) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|          Enquiry Updated!              |");
            System.out.println("+-----------------------------------------+");
        } else {
            System.out.println("\nFailed to update enquiry. Please try again later.");
        }
    }

    /**
     * Allows the applicant to delete an existing enquiry.
     * Confirms the action before deletion.
     */
    private void deleteEnquiry() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|        DELETE ENQUIRY       |");
        System.out.println("+-----------------------------+");

        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(currentUser.getNric());
        if (enquiries.isEmpty()) {
            System.out.println("You don't have any enquiries to delete.");
            return;
        }

        // Print table header
        System.out.println("+-----+--------------------+------------------------------------+----------+");
        System.out.printf("| %-3s | %-18s | %-36s | %-8s |%n",
                "No.", "Project", "Enquiry", "Answered");
        System.out.println("+-----+--------------------+------------------------------------+----------+");

        // Print enquiry details
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            String enquiryText = enquiry.getEnquiryText();
            if (enquiryText.length() > 36) {
                enquiryText = enquiryText.substring(0, 33) + "...";
            }

            System.out.printf("| %-3d | %-18s | %-36s | %-8s |%n",
                    i + 1,
                    enquiry.getProjectName(),
                    enquiryText,
                    enquiry.isAnswered() ? "Yes" : "No");
        }

        System.out.println("+-----+--------------------+------------------------------------+----------+");

        // Let user select an enquiry
        System.out.print("\nEnter the number of the enquiry to delete (0 to cancel): ");
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

        // Display enquiry details
        System.out.println("\nEnquiry to Delete:");
        System.out.println("Project: " + selectedEnquiry.getProjectName());
        System.out.println("Enquiry: " + selectedEnquiry.getEnquiryText());
        System.out.println("Status: " + (selectedEnquiry.isAnswered() ? "Answered" : "Pending"));

        // Confirm deletion
        System.out.print("\nAre you sure you want to delete this enquiry? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean deleted = enquiryController.deleteEnquiry(selectedEnquiry.getEnquiryId());

            if (deleted) {
                System.out.println("\n+-----------------------------------------+");
                System.out.println("|          Enquiry Deleted!              |");
                System.out.println("+-----------------------------------------+");
            } else {
                System.out.println("\nFailed to delete enquiry. Please try again later.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Allows the applicant to update their filter settings.
     * Sets neighborhood and flat type filters for project views.
     * 
     * @param currentNeighborhood The current neighborhood filter
     * @param currentFlatType     The current flat type filter
     * @return An array containing the updated filters
     */
    private Object[] updateFilterSettings(String currentNeighborhood, FlatType currentFlatType) {
        System.out.println("\n+-----------------------------+");
        System.out.println("|     UPDATE FILTER SETTINGS    |");
        System.out.println("+-----------------------------+");

        System.out.println("Current Filters:");
        System.out.println("Neighborhood: " + (currentNeighborhood != null ? currentNeighborhood : "None"));
        System.out.println("Flat Type: " + (currentFlatType != null ? currentFlatType.getDescription() : "None"));

        // Get all available neighborhoods for selection
        List<Project> allProjects = projectController.getAllProjects();
        java.util.Set<String> neighborhoods = new java.util.HashSet<>();

        for (Project project : allProjects) {
            neighborhoods.add(project.getNeighborhood());
        }

        // Convert to sorted list
        List<String> sortedNeighborhoods = new java.util.ArrayList<>(neighborhoods);
        java.util.Collections.sort(sortedNeighborhoods);

        // Display neighborhoods
        if (!sortedNeighborhoods.isEmpty()) {
            System.out.println("\nAvailable Neighborhoods:");
            for (int i = 0; i < sortedNeighborhoods.size(); i++) {
                System.out.println((i + 1) + ". " + sortedNeighborhoods.get(i));
            }
            System.out.println((sortedNeighborhoods.size() + 1) + ". None (Clear filter)");
        }

        // Update neighborhood filter
        System.out.print("\nEnter the number of the neighborhood to filter by (0 to skip): ");
        String newNeighborhood = currentNeighborhood;
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice != 0) {
                if (choice == sortedNeighborhoods.size() + 1) {
                    newNeighborhood = null;
                } else if (choice >= 1 && choice <= sortedNeighborhoods.size()) {
                    newNeighborhood = sortedNeighborhoods.get(choice - 1);
                } else {
                    System.out.println("Invalid choice. Keeping current neighborhood filter.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Keeping current neighborhood filter.");
        }

        // Update flat type filter
        System.out.println("\nSelect flat type filter:");
        System.out.println("1. None (Clear filter)");
        System.out.println("2. 2-Room");
        System.out.println("3. 3-Room");
        System.out.print("Enter your choice (0 to skip): ");

        FlatType newFlatType = currentFlatType;
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice != 0) {
                switch (choice) {
                    case 1:
                        newFlatType = null;
                        break;
                    case 2:
                        newFlatType = FlatType.TWO_ROOM;
                        break;
                    case 3:
                        newFlatType = FlatType.THREE_ROOM;
                        break;
                    default:
                        System.out.println("Invalid choice. Keeping current flat type filter.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Keeping current flat type filter.");
        }

        System.out.println("\n+-----------------------------------------+");
        System.out.println("|        Filters Updated!                |");
        System.out.println("+-----------------------------------------+");
        System.out.println("New Filters:");
        System.out.println("Neighborhood: " + (newNeighborhood != null ? newNeighborhood : "None"));
        System.out.println("Flat Type: " + (newFlatType != null ? newFlatType.getDescription() : "None"));

        return new Object[] { newNeighborhood, newFlatType };
    }

    /**
     * Creates a new enquiry about a specific project.
     * Used when viewing project details.
     * 
     * @param projectName The name of the project
     */
    private void createEnquiry(String projectName) {
        System.out.println("\n+-----------------------------+");
        System.out.println("|        CREATE ENQUIRY       |");
        System.out.println("+-----------------------------+");
        System.out.println("Project: " + projectName);

        System.out.println("\nEnter your enquiry:");
        System.out.println("(Type your question below, press Enter twice when finished)");

        StringBuilder enquiryTextBuilder = new StringBuilder();
        String line;
        boolean doubleEnter = false;

        while (!doubleEnter) {
            line = scanner.nextLine();
            if (line.isEmpty() && enquiryTextBuilder.length() > 0) {
                // Empty line after some content - check for double Enter
                doubleEnter = true;
            } else {
                if (enquiryTextBuilder.length() > 0) {
                    enquiryTextBuilder.append("\n");
                }
                enquiryTextBuilder.append(line);
                doubleEnter = false;
            }
        }

        String enquiryText = enquiryTextBuilder.toString().trim();

        if (enquiryText.isEmpty()) {
            System.out.println("Enquiry cannot be empty. Operation cancelled.");
            return;
        }

        String enquiryId = enquiryController.createEnquiry(
                currentUser.getNric(), projectName, enquiryText);

        if (enquiryId != null) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|        Enquiry Submitted!              |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enquiry ID: " + enquiryId);
            System.out.println("You will be notified when there is a response.");
        } else {
            System.out.println("\nFailed to submit enquiry. Please try again later.");
        }
    }

    /**
     * Checks if the applicant has an application for a specific project.
     * Used to determine if a non-visible project should still be viewable.
     * 
     * @param applicantNric The NRIC of the applicant
     * @param projectName   The name of the project
     * @return true if the applicant has an application for the project, false
     *         otherwise
     */
    private boolean hasApplicationForProject(String applicantNric, String projectName) {
        Application application = applicationController.getCurrentApplication(applicantNric);
        return application != null && application.getProjectName().equals(projectName);
    }
}