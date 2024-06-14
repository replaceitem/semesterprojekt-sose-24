package org.driveractivity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecificConditionType {
    BEGIN_FT(true, Condition.FT),
    END_FT(false, Condition.FT),
    BEGIN_OUT_OF_SCOPE(true, Condition.OUT_OF_SCOPE),
    END_OUT_OF_SCOPE(false, Condition.OUT_OF_SCOPE),
    INVALID(false, null);
    
    private final boolean isBegin;
    private final Condition condition;

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

    public enum Condition {
        FT,
        OUT_OF_SCOPE
    }
}
