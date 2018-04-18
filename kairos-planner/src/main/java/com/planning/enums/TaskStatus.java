package com.planning.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {

    VISITATED("Visitated"),GENERATED("Generated"),PLANNED("Planned"),DELIVERED("Delivered"),CANCELLED("Cancelled"),CONFIRMED("Confirmed"),INCOMPLETE("In_Complete"),RECORDED("Recorded"),
    FIXED("Fixed"),
    DRIVING("Driving"),
    ARRIVED("Arrived"),
    FINISHED("Finished"),
    CUSTOMER_ABSENT("Customer_Absent"),REFUSED("Refused"),ABORTED("Aborted");

    private String value;

    private TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static TaskStatus getEnumByString(String status) {
        for (TaskStatus is : TaskStatus.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }

}
