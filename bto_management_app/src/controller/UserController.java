package controller;

import data.UserDB;
import entity.*;
import entity.enums.MaritalStatus;
// import entity.enums.UserRole;
import entity.enums.UserRole;

import java.util.ArrayList;
import java.util.List;
// import java.util.UUID;

/**
 * Controller for managing user data and operations.
 */
public class UserController {
    private UserDB userDB;

    /**
     * Constructs a new UserController with a reference to the user database.
     * 
     * @param userDB The user database
     */
    public UserController(UserDB userDB) {
        this.userDB = userDB;
    }

    /**
     * Gets a user by NRIC.
     * 
     * @param nric The NRIC of the user to get
     * @return The user, or null if not found
     */
    public User getUser(String nric) {
        return userDB.getUser(nric);
    }

    /**
     * Gets all users in the system.
     * 
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return userDB.getAllUsers();
    }

    /**
     * Gets all users with a specific role.
     * 
     * @param role The role to filter by
     * @return A list of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        List<User> users = userDB.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getRole() == role) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    /**
     * Gets all applicants in the system.
     * 
     * @return A list of all applicants
     */
    public List<Applicant> getAllApplicants() {
        return userDB.getAllApplicants();
    }

    /**
     * Gets all HDB officers in the system.
     * 
     * @return A list of all HDB officers
     */
    public List<HDBOfficer> getAllOfficers() {
        return userDB.getAllOfficers();
    }

    /**
     * Gets all HDB managers in the system.
     * 
     * @return A list of all HDB managers
     */
    public List<HDBManager> getAllManagers() {
        return userDB.getAllManagers();
    }

    /**
     * Adds a new applicant to the system.
     * 
     * @param nric          The NRIC of the applicant
     * @param name          The name of the applicant
     * @param password      The password of the applicant
     * @param age           The age of the applicant
     * @param maritalStatus The marital status of the applicant
     * @return true if the applicant was added, false if NRIC already exists
     */
    public boolean addApplicant(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        Applicant applicant = new Applicant(nric, name, password, age, maritalStatus);
        return userDB.addUser(applicant);
    }

    /**
     * Adds a new HDB officer to the system.
     * 
     * @param nric          The NRIC of the officer
     * @param name          The name of the officer
     * @param password      The password of the officer
     * @param age           The age of the officer
     * @param maritalStatus The marital status of the officer
     * @return true if the officer was added, false if NRIC already exists
     */
    public boolean addOfficer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        HDBOfficer officer = new HDBOfficer(nric, name, password, age, maritalStatus);
        return userDB.addUser(officer);
    }

    /**
     * Adds a new HDB manager to the system.
     * 
     * @param nric          The NRIC of the manager
     * @param name          The name of the manager
     * @param password      The password of the manager
     * @param age           The age of the manager
     * @param maritalStatus The marital status of the manager
     * @return true if the manager was added, false if NRIC already exists
     */
    public boolean addManager(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        HDBManager manager = new HDBManager(nric, name, password, age, maritalStatus);
        return userDB.addUser(manager);
    }

    /**
     * Updates an existing user in the system.
     * 
     * @param user The user to update
     * @return true if the user was updated, false if NRIC not found
     */
    public boolean updateUser(User user) {
        return userDB.updateUser(user);
    }

    /**
     * Updates an applicant's current application.
     * 
     * @param nric          The NRIC of the applicant
     * @param applicationId The ID of the application, or null to clear
     * @return true if the applicant was updated, false if NRIC not found
     */
    public boolean updateApplicantApplication(String nric, String applicationId) {
        User user = userDB.getUser(nric);
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            applicant.setCurrentApplicationId(applicationId);
            return userDB.updateUser(applicant);
        }
        return false;
    }

    /**
     * Updates an officer's handling project.
     * 
     * @param nric        The NRIC of the officer
     * @param projectName The name of the project, or null to clear
     * @return true if the officer was updated, false if NRIC not found
     */
    public boolean updateOfficerProject(String nric, String projectName) {
        User user = userDB.getUser(nric);
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            officer.setHandlingProjectName(projectName);
            return userDB.updateUser(officer);
        }
        return false;
    }

    /**
     * Adds a project to a manager's created projects list.
     * 
     * @param nric        The NRIC of the manager
     * @param projectName The name of the project
     * @return true if the project was added, false if NRIC not found
     */
    public boolean addManagerProject(String nric, String projectName) {
        User user = userDB.getUser(nric);
        if (user instanceof HDBManager) {
            HDBManager manager = (HDBManager) user;
            manager.addCreatedProject(projectName);
            return userDB.updateUser(manager);
        }
        return false;
    }

    /**
     * Removes a project from a manager's created projects list.
     * 
     * @param nric        The NRIC of the manager
     * @param projectName The name of the project
     * @return true if the project was removed, false if NRIC not found
     */
    public boolean removeManagerProject(String nric, String projectName) {
        User user = userDB.getUser(nric);
        if (user instanceof HDBManager) {
            HDBManager manager = (HDBManager) user;
            manager.removeCreatedProject(projectName);
            return userDB.updateUser(manager);
        }
        return false;
    }
}