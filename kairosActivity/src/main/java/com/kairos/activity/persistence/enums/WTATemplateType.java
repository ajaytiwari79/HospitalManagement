package com.kairos.activity.persistence.enums;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public enum WTATemplateType {
    CareDayCheck("Care Day Check"),
    AverageScheduledTime("Average Sheduled Time"),
    ConsecutiveWorking("Consecutive Working"),
    DaysOffInPeriod("Days Off In Period"),
    NumberOfNightsAndDays("Number Of Nights And Days"),
    SeniorDaysInYear("Maximum Senior Days In Year"),
    ShiftLength("Shift length"),
    ShiftsInInterval("Shifts in Interval"),
    TimeBank("Time Bank"),
    VetoPerPeriod("Veto per period"),
    ConsecutiveNights("Consecutive Nights"),
    DailyRestingTime("Daily Resting Time"),
    DurationBetweenShift("Duration Between Shifts"),
    RestInConsecutiveDays("Rest In Consecutive Days"),
    WeeklyRestPeriod("Weekly Rest Period"),
    NumberOfWeekendShiftInPeriod("Number Of Weekend Shift In Period"),
    ShortestAndAverageDailyRest("Shortest And Average Daily Rest");

    private String value;

    WTATemplateType(String value) {
        this.value = value;
    }
    
}
