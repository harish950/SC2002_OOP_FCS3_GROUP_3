package boundary;

import controller.*;
import data.*;
import entity.User;
// import entity.enums.UserRole;

import java.util.Scanner;

/**
 * Main user interface for the BTO Management System.
 * Handles program flow and user role-based redirection.
 */
public class MainUI {
    private Scanner scanner;
    private UserDB userDB;
    private ProjectDB projectDB;
    private ApplicationDB applicationDB;
    private EnquiryDB enquiryDB;

    private LoginController loginController;
    private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;
    private ReportController reportController;

    private LoginUI loginUI;
    private ApplicantUI applicantUI;
    private HDBOfficerUI hdbOfficerUI;
    private HDBManagerUI hdbManagerUI;
    private AdminUI adminUI;

    /**
     * Constructs a new MainUI and initializes all components.
     */
    public MainUI() {
        scanner = new Scanner(System.in);

        // Initialize databases
        userDB = new UserDB();
        projectDB = new ProjectDB();
        applicationDB = new ApplicationDB();
        enquiryDB = new EnquiryDB();

        // Initialize controllers
        loginController = new LoginController(userDB);
        userController = new UserController(userDB);
        projectController = new ProjectController(projectDB, userDB);
        applicationController = new ApplicationController(applicationDB, projectDB, userDB);
        enquiryController = new EnquiryController(enquiryDB, projectDB, userDB);
        reportController = new ReportController(applicationDB, projectDB, userDB);

        // Initialize UI components
        loginUI = new LoginUI(scanner, loginController);
        applicantUI = new ApplicantUI(scanner, userController, projectController,
                applicationController, enquiryController, loginUI);
        hdbOfficerUI = new HDBOfficerUI(scanner, userController, projectController,
                applicationController, enquiryController, loginUI);
        hdbManagerUI = new HDBManagerUI(scanner, userController, projectController,
                applicationController, enquiryController, reportController, loginUI);
        adminUI = new AdminUI(scanner, userController, projectController, applicationController);

        // Load sample data if needed
        DataInitializer initializer = new DataInitializer(userDB, projectDB, applicationDB, enquiryDB);
        initializer.initialize();
    }

    /**
     * Starts the BTO Management System.
     */
    public void start() {
        System.out.println("===============================");
        System.out.println("  BTO MANAGEMENT SYSTEM");
        System.out.println("===============================");

        boolean exit = false;

        while (!exit) {
            // Login or exit
            int choice = loginUI.displayLoginMenu();

            switch (choice) {
                case 1: // Login
                    boolean loggedIn = loginUI.login();
                    if (loggedIn) {
                        User currentUser = loginController.getCurrentUser();
                        redirectToUserInterface(currentUser);
                    }
                    break;
                case 2: // Exit
                    exit = true;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Redirects to the appropriate user interface based on user role.
     * 
     * @param user The current user
     */
    private void redirectToUserInterface(User user) {
        if (user == null) {
            return;
        }

        switch (user.getRole()) {
            case APPLICANT:
                applicantUI.setCurrentUser(user);
                applicantUI.displayMainMenu();
                break;
            case HDB_OFFICER:
                // Ask if officer wants to login as an APPLICANT
                System.out.println("Do you want to login as an APPLICANT? (Y/N)");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine().trim().toUpperCase();

                if (response.equals("Y")) {
                    applicantUI.setCurrentUser(user);
                    applicantUI.displayMainMenu();
                } else {
                    hdbOfficerUI.setCurrentUser(user);
                    hdbOfficerUI.displayMainMenu();
                }
                break;
            case HDB_MANAGER:
                hdbManagerUI.setCurrentUser(user);
                hdbManagerUI.displayMainMenu();
                break;
            case ADMIN:
                adminUI.setCurrentUser(user);
                adminUI.displayMainMenu();
                break;
            default:
                System.out.println("error");
        }
    }
}