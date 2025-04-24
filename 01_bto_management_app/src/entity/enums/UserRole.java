package entity.enums;

public enum UserRole {
    APPLICANT("Applicant"),
    HDB_OFFICER("HDB Officer"),
    HDB_MANAGER("HDB Manager"),
    ADMIN("Administrator");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}