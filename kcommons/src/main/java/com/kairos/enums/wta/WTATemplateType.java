package com.kairos.enums.wta;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public enum WTATemplateType {
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
    WTA_FOR_BREAKS_IN_SHIFT("WTA for breaks in shift");



    private String value;

    WTATemplateType(String value) {
        this.value = value;
    }

    public static WTATemplateType getByTemplateType(String templateType) {
        for(WTATemplateType r: WTATemplateType.values()) {
            if(r.value.equals(templateType)) {
                return r;
            }
        }
        return null;
    }
    
}
