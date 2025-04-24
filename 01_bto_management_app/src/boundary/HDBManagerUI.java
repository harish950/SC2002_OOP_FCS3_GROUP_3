// HDBManagerUI.java
package boundary;

import controller.*;
import entity.*;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import entity.enums.MaritalStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * User interface for HDB manager functionality.
 */
public class HDBManagerUI {
    private Scanner scanner;
    private User currentUser;
    private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;
    private ReportController reportController;
    private ProjectUI projectUI;

    private LoginUI loginUI;

    /**
     * Constructs a new HDBManagerUI with references to necessary components.
     * 
     * @param scanner               The scanner for user input
     * @param userController        The user controller
     * @param projectController     The project controller
     * @param applicationController The application controller
     * @param enquiryController     The enquiry controller
     * @param reportController      The report controller
     */
    public HDBManagerUI(Scanner scanner, UserController userController,
            ProjectController projectController,
            ApplicationController applicationController,
            EnquiryController enquiryController,
            ReportController reportController, LoginUI loginUI) {
        this.scanner = scanner;
        this.userController = userController;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.reportController = reportController;
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
     * Displays the main menu for HDB managers and handles user choices.
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
            System.out.println("|     HDB MANAGER MAIN MENU   |");
            System.out.println("+-----------------------------+");
            System.out.println("1. Create New Project");
            System.out.println("2. Edit Project");
            System.out.println("3. Delete Project");
            System.out.println("4. View All Projects");
            System.out.println("5. View My Projects");
            System.out.println("6. Toggle Project Visibility");
            System.out.println("7. Manage Officer Registrations");
            System.out.println("8. Manage Applications");
            System.out.println("9. Manage Withdrawal Requests");
            System.out.println("10. Generate Reports");
            System.out.println("11. View and Reply to Enquiries");
            System.out.println("12. Filter Settings");
            System.out.println("13. Change Password");
            System.out.println("14. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        createNewProject();
                        break;
                    case 2:
                        editProject();
                        break;
                    case 3:
                        deleteProject();
                        break;
                    case 4:
                        viewAllProjects(filterNeighborhood, filterFlatType);
                        break;
                    case 5:
                        viewMyProjects(filterNeighborhood, filterFlatType);
                        break;
                    case 6:
                        toggleProjectVisibility();
                        break;
                    case 7:
                        manageOfficerRegistrations();
                        break;
                    case 8:
                        manageApplications();
                        break;
                    case 9:
                        manageWithdrawalRequests();
                        break;
                    case 10:
                        generateReports();
                        break;
                    case 11:
                        viewAndReplyToEnquiries();
                        break;
                    case 12:
                        Object[] filters = updateFilterSettings(filterNeighborhood, filterFlatType);
                        filterNeighborhood = (String) filters[0];
                        filterFlatType = (FlatType) filters[1];
                        break;
                    case 13:
                        loginUI.changePassword();
                        break;
                    case 14:
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
     * Allows the manager to create a new project.
     */
    private void createNewProject() {
        System.out.println("\n=== CREATE NEW PROJECT ===");

        // Check if manager is already handling a project in application period
        List<Project> allProjects = projectController.getAllProjects();
        HDBManager manager = (HDBManager) currentUser;

        // Get project details
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty. Operation cancelled.");
            return;
        }

        // Check if project name already exists
        if (projectController.getProject(projectName) != null) {
            System.out.println("A project with this name already exists. Please choose a different name.");
            return;
        }

        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine().trim();
        if (neighborhood.isEmpty()) {
            System.out.println("Neighborhood cannot be empty. Operation cancelled.");
            return;
        }

        int twoRoomUnits;
        int threeRoomUnits;
        try {
            System.out.print("Enter number of 2-Room units: ");
            twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            if (twoRoomUnits < 0) {
                System.out.println("Number of units cannot be negative. Operation cancelled.");
                return;
            }

            System.out.print("Enter number of 3-Room units: ");
            threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            if (threeRoomUnits < 0) {
                System.out.println("Number of units cannot be negative. Operation cancelled.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number. Operation cancelled.");
            return;
        }

        LocalDate applicationOpeningDate;
        LocalDate applicationClosingDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            System.out.print("Enter application opening date (YYYY-MM-DD): ");
            applicationOpeningDate = LocalDate.parse(scanner.nextLine().trim(), formatter);

            System.out.print("Enter application closing date (YYYY-MM-DD): ");
            applicationClosingDate = LocalDate.parse(scanner.nextLine().trim(), formatter);

            if (applicationClosingDate.isBefore(applicationOpeningDate)) {
                System.out.println("Closing date cannot be before opening date. Operation cancelled.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD. Operation cancelled.");
            return;
        }

        // Check if manager has active projects in this period
        if (manager.hasActiveProjectInPeriod(allProjects, applicationOpeningDate, applicationClosingDate)) {
            System.out.println("You already have a project in this application period. Operation cancelled.");
            return;
        }

        int availableOfficerSlots;
        try {
            System.out.print("Enter number of available officer slots (max 10): ");
            availableOfficerSlots = Integer.parseInt(scanner.nextLine().trim());
            if (availableOfficerSlots < 1 || availableOfficerSlots > 10) {
                System.out.println("Number of slots must be between 1 and 10. Using default value of 5.");
                availableOfficerSlots = 5;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default value of 5.");
            availableOfficerSlots = 5;
        }

        // Create the project
        boolean success = projectController.createProject(
                projectName, neighborhood, twoRoomUnits, threeRoomUnits,
                applicationOpeningDate, applicationClosingDate, currentUser.getNric(), availableOfficerSlots);

        if (success) {
            System.out.println("\nProject created successfully!");
            System.out.println("Project Name: " + projectName);
            System.out.println("Neighborhood: " + neighborhood);
            System.out.println("2-Room Units: " + twoRoomUnits);
            System.out.println("3-Room Units: " + threeRoomUnits);
            System.out.println("Application Period: " + applicationOpeningDate + " to " + applicationClosingDate);
            System.out.println("Available Officer Slots: " + availableOfficerSlots);
            System.out.println("Visibility: OFF (default)");

            // Ask if manager wants to toggle visibility
            System.out.print("\nDo you want to make this project visible to applicants now? (Y/N): ");
            String toggleVisibility = scanner.nextLine().trim();

            if (toggleVisibility.equalsIgnoreCase("Y")) {
                projectController.toggleProjectVisibility(projectName, true);
                System.out.println("Project visibility toggled ON.");
            }
        } else {
            System.out.println("\nFailed to create project. Please try again later.");
        }
    }

    /**
     * Allows the manager to edit an existing project.
     */
    private void editProject() {
        System.out.println("\n=== EDIT PROJECT ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to edit.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood() +
                    " (" + (project.isVisible() ? "Visible" : "Hidden") + ")");
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to edit (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        // Get updated project details
        System.out.println("\nEditing Project: " + selectedProject.getProjectName());
        System.out.println("(Press Enter to keep current values)");

        System.out.print("Enter new neighborhood (current: " + selectedProject.getNeighborhood() + "): ");
        String neighborhood = scanner.nextLine().trim();
        if (neighborhood.isEmpty()) {
            neighborhood = selectedProject.getNeighborhood();
        }

        int twoRoomUnits = selectedProject.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0);
        int threeRoomUnits = selectedProject.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0);

        try {
            System.out.print("Enter new number of 2-Room units (current: " + twoRoomUnits + "): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                twoRoomUnits = Integer.parseInt(input);
                if (twoRoomUnits < 0) {
                    System.out.println("Number of units cannot be negative. Using current value.");
                    twoRoomUnits = selectedProject.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0);
                }
            }

            System.out.print("Enter new number of 3-Room units (current: " + threeRoomUnits + "): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                threeRoomUnits = Integer.parseInt(input);
                if (threeRoomUnits < 0) {
                    System.out.println("Number of units cannot be negative. Using current value.");
                    threeRoomUnits = selectedProject.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using current values.");
        }

        LocalDate applicationOpeningDate = selectedProject.getApplicationOpeningDate();
        LocalDate applicationClosingDate = selectedProject.getApplicationClosingDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            System.out.print("Enter new application opening date (current: " + applicationOpeningDate + "): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                applicationOpeningDate = LocalDate.parse(input, formatter);
            }

            System.out.print("Enter new application closing date (current: " + applicationClosingDate + "): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                applicationClosingDate = LocalDate.parse(input, formatter);
            }

            if (applicationClosingDate.isBefore(applicationOpeningDate)) {
                System.out.println("Closing date cannot be before opening date. Using current values.");
                applicationOpeningDate = selectedProject.getApplicationOpeningDate();
                applicationClosingDate = selectedProject.getApplicationClosingDate();
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using current values.");
        }

        int availableOfficerSlots = selectedProject.getAvailableOfficerSlots();
        try {
            System.out.print("Enter new number of available officer slots (current: " + availableOfficerSlots + "): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                availableOfficerSlots = Integer.parseInt(input);
                if (availableOfficerSlots < 1 || availableOfficerSlots > 10) {
                    System.out.println("Number of slots must be between 1 and 10. Using current value.");
                    availableOfficerSlots = selectedProject.getAvailableOfficerSlots();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using current value.");
        }

        // Update the project
        boolean success = projectController.updateProject(
                selectedProject.getProjectName(), neighborhood, twoRoomUnits, threeRoomUnits,
                applicationOpeningDate, applicationClosingDate, availableOfficerSlots);

        if (success) {
            System.out.println("\nProject updated successfully!");
            System.out.println("Project Name: " + selectedProject.getProjectName());
            System.out.println("Neighborhood: " + neighborhood);
            System.out.println("2-Room Units: " + twoRoomUnits);
            System.out.println("3-Room Units: " + threeRoomUnits);
            System.out.println("Application Period: " + applicationOpeningDate + " to " + applicationClosingDate);
            System.out.println("Available Officer Slots: " + availableOfficerSlots);
        } else {
            System.out.println("\nFailed to update project. Please try again later.");
        }
    }

    /**
     * Allows the manager to delete an existing project.
     */
    private void deleteProject() {
        System.out.println("\n=== DELETE PROJECT ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to delete.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood() +
                    " (" + (project.isVisible() ? "Visible" : "Hidden") + ")");
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to delete (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        // Confirm deletion
        System.out.println("\nYou are about to delete project: " + selectedProject.getProjectName());
        System.out.print("Are you sure? This action cannot be undone. (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = projectController.deleteProject(selectedProject.getProjectName());

            if (success) {
                System.out.println("\nProject deleted successfully!");
            } else {
                System.out.println("\nFailed to delete project. Please try again later.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Displays all projects with optional filtering.
     * 
     * @param filterNeighborhood The neighborhood filter
     * @param filterFlatType     The flat type filter
     */
    private void viewAllProjects(String filterNeighborhood, FlatType filterFlatType) {
        System.out.println("\n=== ALL PROJECTS ===");

        List<Project> projects = projectController.getAllProjects();

        // Apply filters if set
        if (filterNeighborhood != null && !filterNeighborhood.isEmpty()) {
            projects = projectController.filterByNeighborhood(projects, filterNeighborhood);
        }

        if (filterFlatType != null) {
            projects = projectController.filterByFlatType(projects, filterFlatType);
        }

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
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

        // Print table header with box drawing
        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s | %-13s | %-13s | %-8s |%n",
                "No.", "Project Name", "Neighborhood", "2-Room Units", "3-Room Units",
                "Opening Date", "Closing Date", "Visibility");
        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");

        // Print project details with numbering
        int index = 1;
        for (Project project : projects) {
            System.out.printf("| %-3d | %-18s | %-13s | %-13d | %-13d | %-13s | %-13s | %-8s |%n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0),
                    project.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.isVisible() ? "ON" : "OFF");
        }

        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");

        // Option to view a specific project by number
        System.out.print("\nEnter the number of the project you wish to view (0 to return): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            } else if (selection > 0 && selection <= projects.size()) {
                String projectName = projects.get(selection - 1).getProjectName();
                projectUI.viewProjectDetails(projectName, true);
            } else {
                System.out.println("Invalid project number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Displays projects created by the current manager with optional filtering.
     * 
     * @param filterNeighborhood The neighborhood filter
     * @param filterFlatType     The flat type filter
     */
    private void viewMyProjects(String filterNeighborhood, FlatType filterFlatType) {
        System.out.println("\n=== MY PROJECTS ===");

        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        // Apply filters if set
        if (filterNeighborhood != null && !filterNeighborhood.isEmpty()) {
            managerProjects = projectController.filterByNeighborhood(managerProjects, filterNeighborhood);
        }

        if (filterFlatType != null) {
            managerProjects = projectController.filterByFlatType(managerProjects, filterFlatType);
        }

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects.");
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

        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");
        System.out.printf("| %-3s | %-18s | %-13s | %-13s | %-13s | %-13s | %-13s | %-8s |%n",
                "No.", "Project Name", "Neighborhood", "2-Room Units", "3-Room Units",
                "Opening Date", "Closing Date", "Visibility");
        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");

        // Print project details with numbering
        int index = 1;
        for (Project project : managerProjects) {
            System.out.printf("| %-3d | %-18s | %-13s | %-13d | %-13d | %-13s | %-13s | %-8s |%n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getFlatTypeUnits().getOrDefault(FlatType.TWO_ROOM, 0),
                    project.getFlatTypeUnits().getOrDefault(FlatType.THREE_ROOM, 0),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.isVisible() ? "ON" : "OFF");
        }

        System.out.println(
                "+-----+--------------------+---------------+---------------+---------------+---------------+---------------+----------+");

        // Option to view a specific project by number
        System.out.print("\nEnter the number of the project you wish to view (0 to return): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            } else if (selection > 0 && selection <= managerProjects.size()) {
                String projectName = managerProjects.get(selection - 1).getProjectName();
                projectUI.viewProjectDetails(projectName, true);
            } else {
                System.out.println("Invalid project number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }

    }

    /**
     * Allows the manager to toggle the visibility of a project.
     */
    private void toggleProjectVisibility() {
        System.out.println("\n=== TOGGLE PROJECT VISIBILITY ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to toggle visibility.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood() +
                    " (Currently " + (project.isVisible() ? "Visible" : "Hidden") + ")");
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to toggle visibility (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        // Toggle visibility
        boolean newVisibility = !selectedProject.isVisible();
        boolean success = projectController.toggleProjectVisibility(
                selectedProject.getProjectName(), newVisibility);

        if (success) {
            System.out.println("\nProject visibility toggled successfully!");
            System.out.println("Project: " + selectedProject.getProjectName());
            System.out.println("Visibility: " + (newVisibility ? "ON" : "OFF"));
        } else {
            System.out.println("\nFailed to toggle project visibility. Please try again later.");
        }
    }

    /**
     * Allows the manager to manage officer registrations for their projects.
     */
    private void manageOfficerRegistrations() {
        System.out.println("\n=== MANAGE OFFICER REGISTRATIONS ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects with officer registrations.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - Available Slots: " + project.getAvailableOfficerSlots());
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to manage registrations (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        System.out.println("\nOfficer Registrations for Project: " + selectedProject.getProjectName());

        // Check if there are available slots
        if (selectedProject.getAvailableOfficerSlots() <= 0) {
            System.out.println("This project has no available officer slots.");
            return;
        }

        // Get list of officers
        List<HDBOfficer> allOfficers = userController.getAllOfficers();

        // Filter out officers already handling projects
        List<HDBOfficer> availableOfficers = new java.util.ArrayList<>();
        for (HDBOfficer officer : allOfficers) {
            if (!officer.isHandlingProject()) {
                availableOfficers.add(officer);
            }
        }

        if (availableOfficers.isEmpty()) {
            System.out.println("No available officers found for registration.");
            return;
        }

        System.out.println("\nAvailable Officers:");
        for (int i = 0; i < availableOfficers.size(); i++) {
            HDBOfficer officer = availableOfficers.get(i);
            System.out.println((i + 1) + ". NRIC: " + officer.getNric() +
                    " - Age: " + officer.getAge() +
                    " - Marital Status: " + officer.getMaritalStatus().getStatus());
        }

        // Let user select an officer
        System.out.print("\nEnter the number of the officer to register (0 to cancel): ");
        int officerIndex;
        try {
            officerIndex = Integer.parseInt(scanner.nextLine().trim());
            if (officerIndex == 0) {
                return;
            }
            if (officerIndex < 1 || officerIndex > availableOfficers.size()) {
                System.out.println("Invalid officer number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        HDBOfficer selectedOfficer = availableOfficers.get(officerIndex - 1);

        // Simulate registration
        String registrationId = java.util.UUID.randomUUID().toString();

        System.out.print("\nDo you want to approve this officer registration? (Y/N): ");
        String approve = scanner.nextLine().trim();

        if (approve.equalsIgnoreCase("Y")) {
            boolean success = projectController.approveOfficerRegistration(
                    registrationId, selectedOfficer.getNric(), selectedProject.getProjectName());

            if (success) {
                System.out.println("\nOfficer registration approved successfully!");
                System.out.println("Officer NRIC: " + selectedOfficer.getNric());
                System.out.println("Project: " + selectedProject.getProjectName());
            } else {
                System.out.println("\nFailed to approve officer registration. Please try again later.");
            }
        } else {
            boolean success = projectController.rejectOfficerRegistration(registrationId);
            if (success) {
                System.out.println("\nOfficer registration rejected.");
            } else {
                System.out.println("\nFailed to reject officer registration. Please try again later.");
            }
        }
    }

    /**
     * Allows the manager to manage BTO applications for their projects.
     */
    private void manageApplications() {
        System.out.println("\n=== MANAGE APPLICATIONS ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects with applications.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood());
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to manage applications (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        // Get all applications for this project
        List<Application> applications = applicationController.getApplicationsByProject(
                selectedProject.getProjectName());

        if (applications.isEmpty()) {
            System.out.println("No applications found for this project.");
            return;
        }

        // Filter pending applications
        List<Application> pendingApplications = new java.util.ArrayList<>();
        for (Application application : applications) {
            if (application.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications.add(application);
            }
        }

        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications found for this project.");
            return;
        }

        System.out.println("\nPending Applications for Project: " + selectedProject.getProjectName());
        System.out.printf("%-5s %-20s %-15s %-15s\n",
                "No.", "Application ID", "Applicant NRIC", "Flat Type");
        System.out.println("--------------------------------------------------------");

        for (int i = 0; i < pendingApplications.size(); i++) {
            Application application = pendingApplications.get(i);
            System.out.printf("%-5d %-20s %-15s %-15s\n",
                    i + 1,
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getFlatType().getDescription());
        }

        // Let user select an application
        System.out.print("\nEnter the number of the application to process (0 to cancel): ");
        int applicationIndex;
        try {
            applicationIndex = Integer.parseInt(scanner.nextLine().trim());
            if (applicationIndex == 0) {
                return;
            }
            if (applicationIndex < 1 || applicationIndex > pendingApplications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Application selectedApplication = pendingApplications.get(applicationIndex - 1);

        // Get applicant details
        User applicant = userController.getUser(selectedApplication.getApplicantNric());
        if (applicant == null) {
            System.out.println("Error: Applicant not found.");
            return;
        }

        // Display application details
        System.out.println("\nApplication Details:");
        System.out.println("Application ID: " + selectedApplication.getApplicationId());
        System.out.println("Applicant NRIC: " + applicant.getNric());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus().getStatus());
        System.out.println("Flat Type: " + selectedApplication.getFlatType().getDescription());
        System.out.println("Application Date: " + selectedApplication.getApplicationDate());

        // Check if there are available units
        FlatType flatType = selectedApplication.getFlatType();
        int availableUnits = selectedProject.getFlatTypeUnits().getOrDefault(flatType, 0);

        System.out.println("\nAvailable Units for " + flatType.getDescription() + ": " + availableUnits);

        if (availableUnits <= 0) {
            System.out.println("No available units for this flat type. Application cannot be approved.");
            return;
        }

        // Process application
        System.out.print("\nDo you want to approve this application? (Y/N): ");
        String approve = scanner.nextLine().trim();

        if (approve.equalsIgnoreCase("Y")) {
            boolean success = applicationController.approveApplication(selectedApplication.getApplicationId());

            if (success) {
                System.out.println("\nApplication approved successfully!");
                System.out.println("Applicant will be notified to proceed with flat booking.");
            } else {
                System.out.println("\nFailed to approve application. Please try again later.");
            }
        } else {
            boolean success = applicationController.rejectApplication(selectedApplication.getApplicationId());

            if (success) {
                System.out.println("\nApplication rejected successfully.");
                System.out.println("Applicant will be notified of the rejection.");
            } else {
                System.out.println("\nFailed to reject application. Please try again later.");
            }
        }
    }

    /**
     * Allows the manager to manage withdrawal requests for applications.
     */
    private void manageWithdrawalRequests() {
        System.out.println("\n=== MANAGE WITHDRAWAL REQUESTS ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects with withdrawal requests.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood());
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to manage withdrawals (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        // In a real system, we would fetch withdrawal requests for this project
        // For this implementation, we'll simulate a sample withdrawal request

        System.out.println("\nWithdrawal Requests for Project: " + selectedProject.getProjectName());

        // Get all applications for this project
        List<Application> withdrawalApplications = applicationController.getApplicationsByProject(
                selectedProject.getProjectName()).stream()
                .filter(app -> app.getWithdrawn() == true) // Filter to show only applications with withdrawn = true
                .collect(Collectors.toList());

        if (withdrawalApplications.isEmpty()) {
            System.out.println("No withdrawal requests found for this project.");
            return;
        }

        // Display all withdrawal requests
        System.out.println("\nPending Withdrawal Requests:");
        for (int i = 0; i < withdrawalApplications.size(); i++) {
            Application app = withdrawalApplications.get(i);
            System.out.println((i + 1) + ". Application ID: " + app.getApplicationId());
            System.out.println("   Applicant NRIC: " + app.getApplicantNric());
            System.out.println("   Flat Type: " + app.getFlatType().getDescription());
            System.out.println("   Status: " + app.getStatus().getStatus());
            System.out.println("   ---------------");
        }

        System.out.print("\nEnter the number of the withdrawal request to process (0 to cancel): ");
        int withdrawalIndex;
        try {
            withdrawalIndex = Integer.parseInt(scanner.nextLine().trim());
            if (withdrawalIndex == 0) {
                return;
            }
            if (withdrawalIndex < 1 || withdrawalIndex > withdrawalApplications.size()) {
                System.out.println("Invalid withdrawal request number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Application selectedApplication = withdrawalApplications.get(withdrawalIndex - 1);

        System.out.println("\nSelected Withdrawal Request:");
        System.out.println("Application ID: " + selectedApplication.getApplicationId());
        System.out.println("Applicant NRIC: " + selectedApplication.getApplicantNric());
        System.out.println("Flat Type: " + selectedApplication.getFlatType().getDescription());
        System.out.println("Status: " + selectedApplication.getStatus().getStatus());

        System.out.print("\nDo you want to approve this withdrawal request? (Y/N): ");
        String approve = scanner.nextLine().trim();

        if (approve.equalsIgnoreCase("Y")) {
            boolean success = applicationController.approveWithdrawal(selectedApplication.getApplicationId());

            if (success) {
                System.out.println("\nWithdrawal request approved successfully!");
                System.out.println("Application has been removed from the system.");
            } else {
                System.out.println("\nFailed to approve withdrawal request. Please try again later.");
            }
        } else {
            System.out.println("\nWithdrawal request rejected.");
            System.out.println("Application remains unchanged.");
        }
    }

    /**
     * Allows the manager to generate various reports.
     */
    private void generateReports() {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== GENERATE REPORTS ===");
            System.out.println("1. Application Summary Report");
            System.out.println("2. Booking Details Report");
            System.out.println("3. Flat Type Preference Report");
            System.out.println("4. Remaining Units Report");
            System.out.println("5. Officer Performance Report");
            System.out.println("6. System Summary Report");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        generateApplicationSummaryReport();
                        break;
                    case 2:
                        generateBookingDetailsReport();
                        break;
                    case 3:
                        generateFlatTypePreferenceReport();
                        break;
                    case 4:
                        generateRemainingUnitsReport();
                        break;
                    case 5:
                        generateOfficerPerformanceReport();
                        break;
                    case 6:
                        generateSystemSummaryReport();
                        break;
                    case 7:
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
     * Generates an application summary report.
     */
    private void generateApplicationSummaryReport() {
        System.out.println("\n=== APPLICATION SUMMARY REPORT ===");

        Map<String, Integer> summary = reportController.generateApplicationSummaryReport();

        if (summary.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }

        System.out.printf("%-25s %-15s\n", "Project Name", "Applications");
        System.out.println("--------------------------------------");

        int totalApplications = 0;
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            System.out.printf("%-25s %-15d\n", entry.getKey(), entry.getValue());
            totalApplications += entry.getValue();
        }

        System.out.println("--------------------------------------");
        System.out.printf("%-25s %-15d\n", "TOTAL", totalApplications);
    }

    /**
     * Generates a booking details report for a specific project.
     */
    private void generateBookingDetailsReport() {
        System.out.println("\n=== BOOKING DETAILS REPORT ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects for this report.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " - " + project.getNeighborhood());
        }

        // Let user select a project
        System.out.print("\nEnter the number of the project to generate report (0 to cancel): ");
        int projectIndex;
        try {
            projectIndex = Integer.parseInt(scanner.nextLine().trim());
            if (projectIndex == 0) {
                return;
            }
            if (projectIndex < 1 || projectIndex > managerProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Project selectedProject = managerProjects.get(projectIndex - 1);

        String report = reportController.generateBookingDetailsTextReport(selectedProject.getProjectName());
        System.out.println("\n" + report);
    }

    /**
     * Generates a flat type preference report.
     */
    private void generateFlatTypePreferenceReport() {
        System.out.println("\n=== FLAT TYPE PREFERENCE REPORT ===");

        Map<MaritalStatus, Map<FlatType, Integer>> report = reportController.generateFlatTypePreferenceReport();

        if (report.isEmpty()) {
            System.out.println("No data available for this report.");
            return;
        }

        System.out.println("Flat Type Preferences by Marital Status:");
        System.out.println("----------------------------------------");

        for (Map.Entry<MaritalStatus, Map<FlatType, Integer>> entry : report.entrySet()) {
            MaritalStatus status = entry.getKey();
            Map<FlatType, Integer> preferences = entry.getValue();

            System.out.println("\nMarital Status: " + status.getStatus());
            System.out.printf("%-15s %-15s\n", "Flat Type", "Count");
            System.out.println("-------------------------------");

            int totalForStatus = 0;
            for (Map.Entry<FlatType, Integer> prefEntry : preferences.entrySet()) {
                System.out.printf("%-15s %-15d\n", prefEntry.getKey().getDescription(), prefEntry.getValue());
                totalForStatus += prefEntry.getValue();
            }

            System.out.println("-------------------------------");
            System.out.printf("%-15s %-15d\n", "TOTAL", totalForStatus);
        }
    }

    /**
     * Generates a remaining units report.
     */
    private void generateRemainingUnitsReport() {
        System.out.println("\n=== REMAINING UNITS REPORT ===");

        Map<String, Map<FlatType, Integer>> report = reportController.generateRemainingUnitsReport();

        if (report.isEmpty()) {
            System.out.println("No data available for this report.");
            return;
        }

        System.out.printf("%-25s %-15s %-15s\n", "Project Name", "2-Room Units", "3-Room Units");
        System.out.println("----------------------------------------------------");

        int total2Room = 0;
        int total3Room = 0;

        for (Map.Entry<String, Map<FlatType, Integer>> entry : report.entrySet()) {
            String projectName = entry.getKey();
            Map<FlatType, Integer> units = entry.getValue();

            int units2Room = units.getOrDefault(FlatType.TWO_ROOM, 0);
            int units3Room = units.getOrDefault(FlatType.THREE_ROOM, 0);

            System.out.printf("%-25s %-15d %-15d\n", projectName, units2Room, units3Room);

            total2Room += units2Room;
            total3Room += units3Room;
        }

        System.out.println("----------------------------------------------------");
        System.out.printf("%-25s %-15d %-15d\n", "TOTAL", total2Room, total3Room);
    }

    /**
     * Generates an officer performance report.
     */
    private void generateOfficerPerformanceReport() {
        System.out.println("\n=== OFFICER PERFORMANCE REPORT ===");

        Map<String, Integer> report = reportController.generateOfficerPerformanceReport();

        if (report.isEmpty()) {
            System.out.println("No data available for this report.");
            return;
        }

        System.out.printf("%-15s %-15s\n", "Officer NRIC", "Bookings Processed");
        System.out.println("--------------------------------");

        int totalBookings = 0;
        for (Map.Entry<String, Integer> entry : report.entrySet()) {
            System.out.printf("%-15s %-15d\n", entry.getKey(), entry.getValue());
            totalBookings += entry.getValue();
        }

        System.out.println("--------------------------------");
        System.out.printf("%-15s %-15d\n", "TOTAL", totalBookings);
    }

    /**
     * Generates a system summary report.
     */
    private void generateSystemSummaryReport() {
        System.out.println("\n=== SYSTEM SUMMARY REPORT ===");

        String report = reportController.generateSystemSummaryReport();
        System.out.println("\n" + report);
    }

    /**
     * Allows the manager to view and reply to enquiries about their projects.
     */
    private void viewAndReplyToEnquiries() {
        System.out.println("\n=== VIEW AND REPLY TO ENQUIRIES ===");

        // Get manager's projects
        HDBManager manager = (HDBManager) currentUser;
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getNric());

        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects with enquiries.");
            return;
        }

        // Get all enquiries for manager's projects
        List<Enquiry> allEnquiries = new java.util.ArrayList<>();
        for (Project project : managerProjects) {
            allEnquiries.addAll(enquiryController.getEnquiriesByProject(project.getProjectName()));
        }

        if (allEnquiries.isEmpty()) {
            System.out.println("No enquiries found for your projects.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-40s %-15s\n",
                "No.", "Project", "Applicant NRIC", "Enquiry", "Status");
        System.out.println("------------------------------------------------------------------------------------");

        for (int i = 0; i < allEnquiries.size(); i++) {
            Enquiry enquiry = allEnquiries.get(i);
            String enquiryText = enquiry.getEnquiryText();
            if (enquiryText.length() > 40) {
                enquiryText = enquiryText.substring(0, 37) + "...";
            }

            System.out.printf("%-5d %-20s %-15s %-40s %-15s\n",
                    i + 1,
                    enquiry.getProjectName(),
                    enquiry.getApplicantNric(),
                    enquiryText,
                    enquiry.isAnswered() ? "Answered" : "Pending");
        }

        // Let user select an enquiry
        System.out.print("\nEnter the number of the enquiry to view or reply (0 to return): ");
        int enquiryIndex;
        try {
            enquiryIndex = Integer.parseInt(scanner.nextLine().trim());
            if (enquiryIndex == 0) {
                return;
            }
            if (enquiryIndex < 1 || enquiryIndex > allEnquiries.size()) {
                System.out.println("Invalid enquiry number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        Enquiry selectedEnquiry = allEnquiries.get(enquiryIndex - 1);

        System.out.println("\n=== ENQUIRY DETAILS ===");
        System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("Project: " + selectedEnquiry.getProjectName());
        System.out.println("Applicant NRIC: " + selectedEnquiry.getApplicantNric());
        System.out.println("Submission Time: " + selectedEnquiry.getSubmissionTime());
        System.out.println("\nEnquiry: " + selectedEnquiry.getEnquiryText());

        if (selectedEnquiry.isAnswered()) {
            System.out.println("\nResponse: " + selectedEnquiry.getResponse());

            // Option to update response
            System.out.print("\nWould you like to update the response? (Y/N): ");
            String updateResponse = scanner.nextLine().trim();

            if (updateResponse.equalsIgnoreCase("Y")) {
                System.out.println("Enter new response:");
                String newResponse = scanner.nextLine().trim();

                if (newResponse.isEmpty()) {
                    System.out.println("Response cannot be empty. Operation cancelled.");
                    return;
                }

                boolean answered = enquiryController.answerEnquiry(
                        selectedEnquiry.getEnquiryId(), currentUser.getNric(), newResponse);

                if (answered) {
                    System.out.println("\nResponse updated successfully!");
                } else {
                    System.out.println("\nFailed to update response. Please try again later.");
                }
            }
        } else {
            // Reply to enquiry
            System.out.println("\nEnter your response:");
            String response = scanner.nextLine().trim();

            if (response.isEmpty()) {
                System.out.println("Response cannot be empty. Operation cancelled.");
                return;
            }

            boolean answered = enquiryController.answerEnquiry(
                    selectedEnquiry.getEnquiryId(), currentUser.getNric(), response);

            if (answered) {
                System.out.println("\nResponse submitted successfully!");
            } else {
                System.out.println("\nFailed to submit response. Please try again later.");
            }
        }
    }

    /**
     * Allows the manager to update their filter settings.
     * 
     * @param currentNeighborhood The current neighborhood filter
     * @param currentFlatType     The current flat type filter
     * @return An array containing the updated filters
     */
    private Object[] updateFilterSettings(String currentNeighborhood, FlatType currentFlatType) {
        System.out.println("\n=== UPDATE FILTER SETTINGS ===");

        System.out.println("Current Filters:");
        System.out.println("Neighborhood: " + (currentNeighborhood != null ? currentNeighborhood : "None"));
        System.out.println("Flat Type: " + (currentFlatType != null ? currentFlatType.getDescription() : "None"));

        // Update neighborhood filter
        System.out.print("\nEnter neighborhood filter (or leave blank to clear): ");
        String newNeighborhood = scanner.nextLine().trim();
        if (newNeighborhood.isEmpty()) {
            newNeighborhood = null;
        }

        // Update flat type filter
        System.out.println("\nSelect flat type filter:");
        System.out.println("1. None");
        System.out.println("2. 2-Room");
        System.out.println("3. 3-Room");
        System.out.print("Enter your choice: ");

        FlatType newFlatType = null;
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 2:
                    newFlatType = FlatType.TWO_ROOM;
                    break;
                case 3:
                    newFlatType = FlatType.THREE_ROOM;
                    break;
                default:
                    newFlatType = null;
            }
        } catch (NumberFormatException e) {
            // Invalid input defaults to no filter
            newFlatType = null;
        }

        System.out.println("\nFilters updated successfully!");
        System.out.println("New Filters:");
        System.out.println("Neighborhood: " + (newNeighborhood != null ? newNeighborhood : "None"));
        System.out.println("Flat Type: " + (newFlatType != null ? newFlatType.getDescription() : "None"));

        return new Object[] { newNeighborhood, newFlatType };
    }
}
