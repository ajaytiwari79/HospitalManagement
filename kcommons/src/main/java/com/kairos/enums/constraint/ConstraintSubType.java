package com.kairos.enums.constraint;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConstraintSubType {
    //For Activity
    ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH("ActivityShortestDurationRelativeToShiftLength"),
    MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF("MaximumAllocationPerShiftForThisActivityPerStaff"),
    ACTIVITY_VALID_DAYTYPE("ActivityValidDaytype"),
    ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS("ActivityMustContinuousNumberOfHours"),

    //For Shifts

    //For WTA
    AVERAGE_SCHEDULED_TIME("AverageScheduledTime"),
    CONSECUTIVE_WORKING_PARTOFDAY("ConsecutiveWorkingPartOfDay"),
    DAYS_OFF_IN_PERIOD("DayOfInPeriod"),
    NUMBER_OF_PARTOFDAY("NumberOfPartOfDay"),
    SHIFT_LENGTH("ShiftLength"),
    NUMBER_OF_SHIFTS_IN_INTERVAL("NumberOfShiftsInInterval"),
    TIME_BANK("TimeBank"),
    VETO_PER_PERIOD("VetoPerPeriod"),
    DAILY_RESTING_TIME("DailyRestingTime"),
    DURATION_BETWEEN_SHIFTS("DurationBetweenShifts"),
    REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS("RestInConsecutiveDaysAndNights"),
    WEEKLY_REST_PERIOD("WeeklyRestPeriod"),
    NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD("NumberOfWeekendShiftInPeriod"),
    SHORTEST_AND_AVERAGE_DAILY_REST("ShortestAndAverageDailyRest"),
    SENIOR_DAYS_PER_YEAR("SeniorDaysPerYear"),
    CHILD_CARE_DAYS_CHECK("ChildCareDaysCheck"),
    DAYS_OFF_AFTER_A_SERIES("DaysOffAfterASeries"),
    NO_OF_SEQUENCE_SHIFT("NoOfSequenceShift"),
    EMPLOYEES_WITH_INCREASE_RISK("EmployeeWithIncreaseRisk"),
    WTA_FOR_CARE_DAYS("WtaForCareDays");

    private String value;

    ConstraintSubType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }
}
