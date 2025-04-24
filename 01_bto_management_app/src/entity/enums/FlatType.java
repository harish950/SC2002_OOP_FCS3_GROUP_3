package entity.enums;

public enum FlatType {
    TWO_ROOM("2-Room"),
    THREE_ROOM("3-Room");

    private final String description;

    FlatType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}