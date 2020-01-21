package com.kairos.enums.team;

public enum TeamType {
    MAIN("Main"),SECONDARY("Secondary");
    public String value;

    TeamType(String value) {
        this.value = value;
    }

    public static TeamType getByValue(String value) {
        for (TeamType teamType : TeamType.values()) {
            if (teamType.value.equalsIgnoreCase(value)) {
                return teamType;
            }
        }
        return null;
    }
}
