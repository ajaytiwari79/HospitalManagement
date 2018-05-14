package com.kairos.activity.enums.PriorityGroup;

public enum ShiftSelectionType {
    FIRST_PICK("Pick-First Pick"),
    SHOW_INTEREST("Show Interest"),
    AUTO_ENQUIRIES("Auto Enquiries");

    private String name;

    ShiftSelectionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
