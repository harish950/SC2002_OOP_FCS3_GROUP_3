package boundary;

import controller.LoginController;
import entity.User;

import java.util.Scanner;

/**
 * User interface for login functionality.
 */
public class LoginUI {
    private Scanner scanner;
    private LoginController loginController;

    /**
     * Constructs a new LoginUI with references to necessary components.
     * 
     * @param scanner         The scanner for user input
     * @param loginController The login controller
     */
    public LoginUI(Scanner scanner, LoginController loginController) {
        this.scanner = scanner;
        this.loginController = loginController;
    }

    /**
     * Displays the login menu and gets user choice.
     * 
     * @return The user's choice
     */
    public int displayLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Handles the login process.
     * 
     * @return true if login was successful, false otherwise
     */
    public boolean login() {
        System.out.println("\n=== LOGIN ===");

        String nric = null;
        boolean validNRIC = false;

        // Get valid NRIC
        while (!validNRIC) {
            System.out.print("Enter NRIC: ");
            nric = scanner.nextLine().trim();

            validNRIC = loginController.validateNRIC(nric);
            if (!validNRIC) {
                System.out.println("Invalid NRIC format. NRIC should start with S or T, "
                        + "followed by 7 digits, and end with a letter.");

                System.out.print("Try again? (Y/N): ");
                String retry = scanner.nextLine().trim();
                if (!retry.equalsIgnoreCase("Y")) {
                    return false;
                }
            }
        }

        // Get password
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        boolean loggedIn = loginController.login(nric, password);

        if (loggedIn) {
            User user = loginController.getCurrentUser();
            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + user.getName() + " (" + user.getRole().getRole() + ")");

            if (loginController.isDefaultPassword(user.getNric(), user.getPassword())) {
                System.out.print("\nYou are using the default password. Do change it!\n");
            }
            return true;
        } else {
            System.out.println("Login failed. Invalid NRIC or password.");
            return false;
        }
    }

    /**
     * Handles the password change process.
     */
    public void changePassword() {
        System.out.println("\n=== CHANGE PASSWORD ===");

        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match. Password change cancelled.");
            return;
        }

        boolean changed = loginController.changePassword(currentPassword, newPassword);

        if (changed) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Current password may be incorrect.");
        }
    }
}