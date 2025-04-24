package boundary;

import controller.*;
import entity.*;
import entity.enums.MaritalStatus;
import entity.enums.UserRole;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for administrator functionality.
 * Handles system-wide administrative tasks.
 */
public class AdminUI {
    private Scanner scanner;
    private User currentUser;
    private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;

    /**
     * Constructs a new AdminUI with references to necessary components.
     * 
     * @param scanner               The scanner for user input
     * @param userController        The user controller
     * @param projectController     The project controller
     * @param applicationController The application controller
     */
    public AdminUI(Scanner scanner, UserController userController,
            ProjectController projectController,
            ApplicationController applicationController) {
        this.scanner = scanner;
        this.userController = userController;
        this.projectController = projectController;
        this.applicationController = applicationController;
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
     * Displays the main menu for administrators and handles user choices.
     */
    public void displayMainMenu() {
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            System.out.println("Error: Unauthorized access.");
            return;
        }

        boolean exit = false;

        while (!exit) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|     ADMINISTRATOR MAIN MENU |");
            System.out.println("+-----------------------------+");

            System.out.println("1. View All Users");
            System.out.println("2. Create New User");
            System.out.println("3. System Statistics");
            System.out.println("4. Reset User Password");
            System.out.println("5. Logout");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        viewAllUsers();
                        break;
                    case 2:
                        createNewUser();
                        break;
                    case 3:
                        showSystemStatistics();
                        break;
                    case 4:
                        resetUserPassword();
                        break;
                    case 5:
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
     * Displays all users in the system.
     */
    private void viewAllUsers() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|               ALL USERS                       ");
        System.out.println("+-----------------------------------------------");

        List<User> allUsers = userController.getAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }

        // Filter options
        System.out.println("Filter by Role:");
        System.out.println("1. All Users");
        System.out.println("2. Applicants");
        System.out.println("3. HDB Officers");
        System.out.println("4. HDB Managers");
        System.out.print("\nSelect filter option: ");

        int filterOption;
        try {
            filterOption = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Showing all users.");
            filterOption = 1;
        }

        List<User> filteredUsers;
        String roleFilter = "All Users";

        switch (filterOption) {
            case 2:
                filteredUsers = userController.getUsersByRole(UserRole.APPLICANT);
                roleFilter = "Applicants";
                break;
            case 3:
                filteredUsers = userController.getUsersByRole(UserRole.HDB_OFFICER);
                roleFilter = "HDB Officers";
                break;
            case 4:
                filteredUsers = userController.getUsersByRole(UserRole.HDB_MANAGER);
                roleFilter = "HDB Managers";
                break;
            default:
                filteredUsers = allUsers;
                break;
        }

        if (filteredUsers.isEmpty()) {
            System.out.println("No users found with the selected filter.");
            return;
        }

        System.out.println("\nUsers (" + roleFilter + "): " + filteredUsers.size() + " total");

        // Print table header
        System.out.println("┌─────┬───────────────┬──────────────────┬─────┬────────────────┬──────────────────┐");
        System.out.printf("│ %-3s │ %-13s │ %-16s │ %-3s │ %-14s │ %-16s │%n",
                "No.", "NRIC", "Name", "Age", "Marital Status", "Role");
        System.out.println("├─────┼───────────────┼──────────────────┼─────┼────────────────┼──────────────────┤");

        // Print user details
        for (int i = 0; i < filteredUsers.size(); i++) {
            User user = filteredUsers.get(i);
            System.out.printf("│ %-3d │ %-13s │ %-16s │ %-3d │ %-14s │ %-16s │%n",
                    i + 1,
                    user.getNric(),
                    user.getName(),
                    user.getAge(),
                    user.getMaritalStatus().getStatus(),
                    user.getRole().getRole());
        }

        System.out.println("└─────┴───────────────┴──────────────────┴─────┴────────────────┴──────────────────┘");

        // Option to view a specific user
        System.out.print("\nEnter the number of a user to view details (0 to return): ");
        int userIndex;
        try {
            userIndex = Integer.parseInt(scanner.nextLine().trim());
            if (userIndex == 0) {
                return;
            }
            if (userIndex < 1 || userIndex > filteredUsers.size()) {
                System.out.println("Invalid user number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        User selectedUser = filteredUsers.get(userIndex - 1);
        displayUserDetails(selectedUser);
    }

    /**
     * Displays detailed information about a specific user.
     * 
     * @param user The user to display
     */
    private void displayUserDetails(User user) {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           USER DETAILS                        ");
        System.out.println("+-----------------------------------------------");
        System.out.println("|  NRIC: " + user.getNric());
        System.out.println("|  Name: " + user.getName());
        System.out.println("|  Age: " + user.getAge());
        System.out.println("|  Marital Status: " + user.getMaritalStatus().getStatus());
        System.out.println("|  Role: " + user.getRole().getRole());

        // Additional details based on role
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            System.out.println("|");
            System.out.println("|  Applicant Details:");

            if (applicant.hasApplication()) {
                System.out.println("|  - Current Application: " + applicant.getCurrentApplicationId());

                // Get application details
                Application application = applicationController.getApplication(applicant.getCurrentApplicationId());
                if (application != null) {
                    System.out.println("|  - Project: " + application.getProjectName());
                    System.out.println("|  - Flat Type: " + application.getFlatType().getDescription());
                    System.out.println("|  - Status: " + application.getStatus());
                }
            } else {
                System.out.println("|  - No current application");
            }
        } else if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            System.out.println("|");
            System.out.println("|  Officer Details:");

            if (officer.isHandlingProject()) {
                System.out.println("|  - Handling Project: " + officer.getHandlingProjectName());

                // Get project details
                Project project = projectController.getProject(officer.getHandlingProjectName());
                if (project != null) {
                    System.out.println("|  - Neighborhood: " + project.getNeighborhood());
                    System.out.println("|  - Application Period: " + project.getApplicationOpeningDate() +
                            " to " + project.getApplicationClosingDate());
                }
            } else {
                System.out.println("|  - Not handling any project");
            }
        } else if (user instanceof HDBManager) {
            HDBManager manager = (HDBManager) user;
            System.out.println("|");
            System.out.println("|  Manager Details:");

            List<String> createdProjects = manager.getCreatedProjectNames();
            if (!createdProjects.isEmpty()) {
                System.out.println("|  - Created Projects:");
                for (String projectName : createdProjects) {
                    System.out.println("|    * " + projectName);
                }
            } else {
                System.out.println("|  - No created projects");
            }
        }

        System.out.println("+-----------------------------------------------");
    }

    /**
     * Creates a new user in the system.
     */
    private void createNewUser() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           CREATE NEW USER                     ");
        System.out.println("+-----------------------------------------------");

        // Get user details
        System.out.print("Enter NRIC (starts with S or T, followed by 7 digits, ends with a letter): ");
        String nric = scanner.nextLine().trim();

        // Validate NRIC format
        if (!validateNRIC(nric)) {
            System.out.println("Invalid NRIC format.");
            return;
        }

        // Check if NRIC already exists
        if (userController.getUser(nric) != null) {
            System.out.println("A user with this NRIC already exists.");
            return;
        }

        // Get user name
        System.out.print("Enter full name: ");
        String name = scanner.nextLine().trim();

        // Validate name is not empty
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        // Get user age
        System.out.print("Enter age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
            if (age < 18) {
                System.out.println("Age must be at least 18.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Please enter a number.");
            return;
        }

        // Get marital status
        System.out.println("Select marital status:");
        System.out.println("1. Single");
        System.out.println("2. Married");
        System.out.print("Enter choice: ");

        MaritalStatus maritalStatus;
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    maritalStatus = MaritalStatus.SINGLE;
                    break;
                case 2:
                    maritalStatus = MaritalStatus.MARRIED;
                    break;
                default:
                    System.out.println("Invalid choice. Using Single as default.");
                    maritalStatus = MaritalStatus.SINGLE;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using Single as default.");
            maritalStatus = MaritalStatus.SINGLE;
        }

        // Get user role
        System.out.println("Select user role:");
        System.out.println("1. Applicant");
        System.out.println("2. HDB Officer");
        System.out.println("3. HDB Manager");
        System.out.print("Enter choice: ");

        int roleChoice;
        try {
            roleChoice = Integer.parseInt(scanner.nextLine().trim());
            if (roleChoice < 1 || roleChoice > 4) {
                System.out.println("Invalid choice. Using Applicant as default.");
                roleChoice = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using Applicant as default.");
            roleChoice = 1;
        }

        // Set default password
        String password = "password";

        // Create user based on role
        boolean success = false;
        switch (roleChoice) {
            case 1:
                success = userController.addApplicant(nric, name, password, age, maritalStatus);
                break;
            case 2:
                success = userController.addOfficer(nric, name, password, age, maritalStatus);
                break;
            case 3:
                success = userController.addManager(nric, name, password, age, maritalStatus);
                break;
        }

        if (success) {
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|          User Created!                  |");
            System.out.println("+-----------------------------------------+");
            System.out.println("NRIC: " + nric);
            System.out.println("Default Password: " + password);
            System.out.println("User should change password on first login.");
        } else {
            System.out.println("\nFailed to create user. Please try again later.");
        }
    }

    /**
     * Validates NRIC format.
     * 
     * @param nric The NRIC to validate
     * @return true if valid, false otherwise
     */
    private boolean validateNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }

        char firstChar = nric.charAt(0);
        if (firstChar != 'S' && firstChar != 'T') {
            return false;
        }

        char lastChar = nric.charAt(8);
        if (!Character.isLetter(lastChar)) {
            return false;
        }

        // Check that characters 1-7 are digits
        for (int i = 1; i < 8; i++) {
            if (!Character.isDigit(nric.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Shows system-wide statistics.
     */
    private void showSystemStatistics() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           SYSTEM STATISTICS                  ");
        System.out.println("+-----------------------------------------------");

        // Count users by role
        int applicantCount = userController.getUsersByRole(UserRole.APPLICANT).size();
        int officerCount = userController.getUsersByRole(UserRole.HDB_OFFICER).size();
        int managerCount = userController.getUsersByRole(UserRole.HDB_MANAGER).size();
        int totalUsers = applicantCount + officerCount + managerCount;

        System.out.println("User Statistics:");
        System.out.println("┌────────────────┬───────────┐");
        System.out.println("│ Role           │ Count     │");
        System.out.println("├────────────────┼───────────┤");
        System.out.printf("│ Applicants     │ %-9d │%n", applicantCount);
        System.out.printf("│ HDB Officers   │ %-9d │%n", officerCount);
        System.out.printf("│ HDB Managers   │ %-9d │%n", managerCount);
        System.out.println("├────────────────┼───────────┤");
        System.out.printf("│ Total Users    │ %-9d │%n", totalUsers);
        System.out.println("└────────────────┴───────────┘");

        // Project statistics
        List<Project> allProjects = projectController.getAllProjects();
        int activeProjects = 0;
        int visibleProjects = 0;

        for (Project project : allProjects) {
            if (project.isInApplicationPeriod()) {
                activeProjects++;
            }
            if (project.isVisible()) {
                visibleProjects++;
            }
        }

        System.out.println("\nProject Statistics:");
        System.out.println("┌─────────────────┬───────────┐");
        System.out.println("│ Status          │ Count     │");
        System.out.println("├─────────────────┼───────────┤");
        System.out.printf("│ Total Projects  │ %-9d │%n", allProjects.size());
        System.out.printf("│ Active Projects │ %-9d │%n", activeProjects);
        System.out.printf("│ Visible Projects│ %-9d │%n", visibleProjects);
        System.out.println("└─────────────────┴───────────┘");
    }

    /**
     * Resets a user's password to the default.
     */
    private void resetUserPassword() {
        System.out.println("\n+-----------------------------------------------");
        System.out.println("|           RESET USER PASSWORD                ");
        System.out.println("+-----------------------------------------------");

        System.out.print("Enter user's NRIC: ");
        String nric = scanner.nextLine().trim();

        User user = userController.getUser(nric);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("User found: " + user.getNric() + " (" + user.getRole().getRole() + ")");
        System.out.print("Reset password to default? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            user.setPassword("password");
            boolean success = userController.updateUser(user);

            if (success) {
                System.out.println("\n+-----------------------------------------+");
                System.out.println("|      Password Reset Successfully!       |");
                System.out.println("+-----------------------------------------+");
                System.out.println("User's password has been reset to: password");
                System.out.println("User should change password on next login.");
            } else {
                System.out.println("\nFailed to reset password. Please try again later.");
            }
        } else {
            System.out.println("Password reset cancelled.");
        }
    }
}