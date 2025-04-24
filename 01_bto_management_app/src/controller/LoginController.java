package controller;

import data.UserDB;
import entity.User;

/**
 * Controller for handling user authentication and password management.
 */
public class LoginController {
    private UserDB userDB;
    private User currentUser;

    /**
     * Constructs a new LoginController with a reference to the user database.
     * 
     * @param userDB The user database
     */
    public LoginController(UserDB userDB) {
        this.userDB = userDB;
        this.currentUser = null;
    }

    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param nric     The NRIC of the user
     * @param password The password of the user
     * @return true if authentication was successful, false otherwise
     */
    public boolean login(String nric, String password) {
        User user = userDB.authenticate(nric, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the currently logged-in user.
     * 
     * @return The current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Changes the password of the current user.
     * 
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if the password was changed, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        boolean success = userDB.changePassword(currentUser.getNric(), oldPassword, newPassword);
        if (success) {
            // Update the current user reference
            currentUser = userDB.getUser(currentUser.getNric());
        }
        return success;
    }

    /**
     * Checks if the provided password is equal to the default password.
     * 
     * @param nric     The NIRC to check
     * @param password The password to check
     * @return true if the password is the default password
     */
    public boolean isDefaultPassword(String nric, String password) {
        return userDB.hasDefaultPassword(nric, password);
    }

    /**
     * Validates an NRIC format.
     * 
     * @param nric The NRIC to validate
     * @return true if the NRIC has a valid format, false otherwise
     */
    public boolean validateNRIC(String nric) {
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
}
