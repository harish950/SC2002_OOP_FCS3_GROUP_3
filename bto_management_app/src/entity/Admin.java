package entity;

import entity.enums.MaritalStatus;
import entity.enums.UserRole;

/**
 * Represents an Administrator user in the BTO Management System.
 * Extends the User class with administrator-specific functionality.
 */
public class Admin extends User {

    /**
     * Creates a new AdminUser with the specified details.
     * 
     * @param nric          The NRIC of the administrator
     * @param password      The password of the administrator
     * @param age           The age of the administrator
     * @param maritalStatus The marital status of the administrator
     */
    public Admin(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus, UserRole.ADMIN);
    }
}