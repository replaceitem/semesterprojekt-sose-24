package org.driveractivity.entity;

public enum SpecificConditionType {
    BEGIN_FT, END_FT, BEGIN_OUT_OF_SCOPE, END_OUT_OF_SCOPE, INVALID;

    public String mapNameToString() {
        return switch (this) {
            case BEGIN_FT -> "beginFT";
            case END_FT -> "endFT";
            case BEGIN_OUT_OF_SCOPE -> "beginOutOfScope";
            case END_OUT_OF_SCOPE -> "endOutOfScope";
            default -> null;
        };
    }
    public static SpecificConditionType mapType(String name) {
        if(name == null || name.isEmpty()) {
            return INVALID;
        }
        return switch (name) {
            case "beginFT" -> BEGIN_FT;
            case "endFT" -> END_FT;
            case "beginOutOfScope" -> BEGIN_OUT_OF_SCOPE;
            case "endOutOfScope" -> END_OUT_OF_SCOPE;
            default -> null;
        };
    }
}
