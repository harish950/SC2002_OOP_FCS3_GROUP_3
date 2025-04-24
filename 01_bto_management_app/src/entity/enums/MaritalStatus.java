package entity.enums;

public enum MaritalStatus {
    SINGLE("Single"),
    MARRIED("Married");

    private final String status;

    MaritalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}