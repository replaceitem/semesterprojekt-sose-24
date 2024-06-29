package org.driveractivity.entity;

public enum CardStatus {
    INSERTED, NOT_INSERTED;
    public String mapNameToString() {
        return switch(this) {
            case INSERTED -> "inserted";
            case NOT_INSERTED -> "notInserted";
        };
    }
    public static CardStatus mapNameFromString(String string) {
        return switch(string) {
            case "inserted" -> INSERTED;
            case "notInserted" -> NOT_INSERTED;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }
}
