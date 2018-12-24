package com.kairos.enums.constraint;

public enum ConstraintSubType {
    //For Activity
    ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,
    MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,
    ACTIVITY_VALID_DAYTYPE,
    ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS,

    //For Shifts

    //For WTA
    AVERAGE_SHEDULED_TIME,
    CONSECUTIVE_WORKING_PARTOFDAY,
    DAYS_OFF_IN_PERIOD,
    NUMBER_OF_PARTOFDAY,
    SHIFT_LENGTH,
    NUMBER_OF_SHIFTS_IN_INTERVAL,
    TIME_BANK,
    VETO_PER_PERIOD,
    DAILY_RESTING_TIME,
    DURATION_BETWEEN_SHIFTS,
    REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS,
    WEEKLY_REST_PERIOD,
    NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD,
    SHORTEST_AND_AVERAGE_DAILY_REST,
    SENIOR_DAYS_PER_YEAR,
    CHILD_CARE_DAYS_CHECK,
    DAYS_OFF_AFTER_A_SERIES,
    NO_OF_SEQUENCE_SHIFT,
    EMPLOYEES_WITH_INCREASE_RISK,
    WTA_FOR_CARE_DAYS




    //===========================Variables==========================
    /*private String constraintSubTypeValue;
    private ConstraintType constraintType;

    //=======================Constructor============================
    ConstraintSubType( ConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    //===========================Getters============================
    public String getConstraintSubType() {
        return constraintSubTypeValue;
    }*/
}
