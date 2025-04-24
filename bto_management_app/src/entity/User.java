package entity;

import entity.enums.MaritalStatus;
import entity.enums.UserRole;
import java.io.Serializable;

/**
 * Represents a user in the BTO Management System.
 * Base class for all user types in the system.
 */
public class User implements Serializable {
    private String nric;
    private String name;
    private String password;
    private int age;
    private MaritalStatus maritalStatus;
    private UserRole role;

    /**
     * Creates a new User with the specified details.
     * 
     * @param nric          The NRIC of the user
     * @param name          The name of the user
     * @param password      The password of the user
     * @param age           The age of the user
     * @param maritalStatus The marital status of the user
     * @param role          The role of the user in the system
     */
    public User(String nric, String name, String password, int age, MaritalStatus maritalStatus, UserRole role) {
        this.nric = nric;
        this.name = name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.role = role;
    }

    /**
     * Gets the NRIC of this user.
     * 
     * @return The NRIC of the user
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the name of this user.
     * 
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the password of this user.
     * 
     * @return The password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of this user.
     * 
     * @param password The new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the age of this user.
     * 
     * @return The age of the user
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the marital status of this user.
     * 
     * @return The marital status of the user
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the role of this user.
     * 
     * @return The role of the user
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the role of this user.
     * 
     * @param role The new role for the user
     */
    public void setRole(UserRole role) {
        this.role = role;
    }
}