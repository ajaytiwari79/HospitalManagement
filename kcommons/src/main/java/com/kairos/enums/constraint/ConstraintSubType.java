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
    PRESENCE_AND_ABSENCE_SAME_TIME("Presence and Absence at same time"),
    MAX_SHIFT_OF_STAFF("Maximum shift of staff"),
    FIX_ACTIVITY_SHOULD_NOT_CHANGE("Fix Activity Should not change"),
    MAX_LENGTH_OF_SHIFT_IN_NIGHT_TIMESLOT("Max length of shift in night time slot"),
    DISLIKE_NIGHT_SHIFS_FOR_NON_NIGHT_WORKERS("Dislike night shifts for non night workers"),

    AVERAGE_SHEDULED_TIME("Average Sheduled Time"),
    CONSECUTIVE_WORKING_PARTOFDAY("Consecutive Working"),
    DAYS_OFF_IN_PERIOD("Days Off In Period"),
    NUMBER_OF_PARTOFDAY("Number Of Nights And Days"),
    SHIFT_LENGTH("Shift length"),
    NUMBER_OF_SHIFTS_IN_INTERVAL("Shifts in Interval"),
    TIME_BANK("Time Bank"),
    VETO_AND_STOP_BRICKS("Veto and stop bricks"),
    DAILY_RESTING_TIME("Daily Resting Time"),
    DURATION_BETWEEN_SHIFTS("Duration Between Shifts"),
    REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS("Rest In Consecutive Days and nights"),
    WEEKLY_REST_PERIOD("Weekly Rest Period"),
    NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD("Number Of Weekend Shift In Period"),
    SHORTEST_AND_AVERAGE_DAILY_REST("Shortest And Average Daily Rest"),
    SENIOR_DAYS_PER_YEAR("Maximum Senior Days Per Year"),
    CHILD_CARE_DAYS_CHECK("Child Care Day Check"),
    DAYS_OFF_AFTER_A_SERIES("days off after a series"),
    NO_OF_SEQUENCE_SHIFT("no of sequence shift"),
    EMPLOYEES_WITH_INCREASE_RISK("employees with increase risk"),
    WTA_FOR_CARE_DAYS("WTA for Care days"),
    PROTECTED_DAYS_OFF("WTA for Protected Days Off"),
    WTA_FOR_BREAKS_IN_SHIFT("WTA for breaks in shift");

    private String value;

    ConstraintSubType(String value) {
        this.value = value;
    }

//    @JsonValue
    public String toValue() {
        return value;
    }
}
