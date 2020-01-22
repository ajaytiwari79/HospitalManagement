package com.kairos.enums.constraint;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConstraintSubType {
    //For Activity
    ACTIVITY_LONGEST_DURATION_RELATIVE_TO_SHIFT_LENGTH("Activity Longest Duration Relative To Shift Length"),
    ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH("Activity Shortest Duration Relative To Shift Length"),
    MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF("Maximum Allocation Per Shift For This Activity PerStaff"),
    ACTIVITY_VALID_DAYTYPE("Activity Valid Daytype"),
    ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS("Activity Must Continuous Number Of Hours"),
    MINIMIZE_SHIFT_ON_WEEKENDS("Minimize Number of Shift On Weekend"),
    PREFER_PERMANENT_EMPLOYEE("Prefer Permanent Employee"),
    MINIMUM_LENGTH_OF_ACTIVITY("Minimum Length of Activity"),
    ACTIVITY_REQUIRED_TAG("Activity Required Tag"),

    //For Shifts

    //For WTA
    AVERAGE_SCHEDULED_TIME("Average Scheduled Time"),
    CONSECUTIVE_WORKING_PARTOFDAY("Consecutive Working Part Of Day"),
    DAYS_OFF_IN_PERIOD("Day Of In Period"),
    NUMBER_OF_PARTOFDAY("Number Of Part Of Day"),
    SHIFT_LENGTH("Shift Length"),
    NUMBER_OF_SHIFTS_IN_INTERVAL("Number Of Shifts In Interval"),
    TIME_BANK("Time Bank"),
    VETO_PER_PERIOD("Veto Per Period"),
    DAILY_RESTING_TIME("Daily Resting Time"),
    DURATION_BETWEEN_SHIFTS("Duration Between Shifts"),
    REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS("Rest In Consecutive Days And Nights"),
    WEEKLY_REST_PERIOD("Weekly Rest Period"),
    NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD("Number Of Weekend Shift In Period"),
    SHORTEST_AND_AVERAGE_DAILY_REST("Shortest And Average Daily Rest"),
    SENIOR_DAYS_PER_YEAR("Senior Days Per Year"),
    CHILD_CARE_DAYS_CHECK("Child Care Days Check"),
    DAYS_OFF_AFTER_A_SERIES("Days Off After A Series"),
    NO_OF_SEQUENCE_SHIFT("No Of Sequence Shift"),
    EMPLOYEES_WITH_INCREASE_RISK("Employee With Increase Risk"),
    WTA_FOR_CARE_DAYS("Wta For Care Days");

    private String value;

    ConstraintSubType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }
}
