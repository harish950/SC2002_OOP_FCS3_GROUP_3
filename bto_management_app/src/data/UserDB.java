package data;

import entity.*;
// import entity.enums.MaritalStatus;
import entity.enums.UserRole;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access class for User objects.
 * Handles storage and retrieval of all user types.
 */
public class UserDB {
    private static final String USER_DATA_FILE = "users.dat";
    private Map<String, User> users;

    /**
     * Constructs a new UserDB and loads existing data if available.
     */
    public UserDB() {
        users = new HashMap<>();
        loadData();
    }

    /**
     * Loads user data from file.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // System.out.println("User data file not found. Starting with empty user
            // database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    /**
     * Saves user data to file.
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    /**
     * Adds a new user to the database.
     * 
     * @param user The user to add
     * @return true if the user was added, false if NRIC already exists
     */
    public boolean addUser(User user) {
        if (users.containsKey(user.getNric())) {
            return false;
        }
        users.put(user.getNric(), user);
        saveData();
        return true;
    }

    /**
     * Updates an existing user in the database.
     * 
     * @param user The user to update
     * @return true if the user was updated, false if NRIC not found
     */
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getNric())) {
            return false;
        }
        users.put(user.getNric(), user);
        saveData();
        return true;
    }

    /**
     * Gets a user by NRIC.
     * 
     * @param nric The NRIC of the user to get
     * @return The user, or null if not found
     */
    public User getUser(String nric) {
        return users.get(nric);
    }

    /**
     * Gets all users in the database.
     * 
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Gets all users of a specific role.
     * 
     * @param role The role to filter by
     * @return A list of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole() == role) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    /**
     * Gets all applicants.
     * 
     * @return A list of all applicants
     */
    public List<Applicant> getAllApplicants() {
        List<Applicant> applicants = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Applicant) {
                applicants.add((Applicant) user);
            }
        }
        return applicants;
    }

    /**
     * Gets all HDB officers.
     * 
     * @return A list of all HDB officers
     */
    public List<HDBOfficer> getAllOfficers() {
        List<HDBOfficer> officers = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof HDBOfficer) {
                officers.add((HDBOfficer) user);
            }
        }
        return officers;
    }

    /**
     * Gets all HDB managers.
     * 
     * @return A list of all HDB managers
     */
    public List<HDBManager> getAllManagers() {
        List<HDBManager> managers = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof HDBManager) {
                managers.add((HDBManager) user);
            }
        }
        return managers;
    }

    /**
     * Authenticates a user login.
     * 
     * @param nric     The NRIC of the user
     * @param password The password of the user
     * @return The authenticated user, or null if authentication failed
     */
    public User authenticate(String nric, String password) {
        User user = users.get(nric);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Checks if the current user's password is equal to the default password.
     * 
     * @param nric     The NRIC of the user
     * @param password The password of the user
     * @return true if the current user has the default password
     */
    public boolean hasDefaultPassword(String nric, String password) {
        User user = users.get(nric);
        if (user.getPassword().equals("password")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Changes a user's password.
     * 
     * @param nric        The NRIC of the user
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if the password was changed, false otherwise
     */
    public boolean changePassword(String nric, String oldPassword, String newPassword) {
        User user = users.get(nric);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            saveData();
            return true;
        }
        return false;
    }
}
