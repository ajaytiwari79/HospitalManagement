package com.kairos.activity.persistence.enums;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public enum WTATemplateType {
    CARE_DAYS_CHECK("Care Day Check"),
    AVERAGE_SHEDULED_TIME("Average Sheduled Time"),
    CONSECUTIVE_WORKING_PARTOFDAY("Consecutive Working"),
    DAYS_OFF_IN_PERIOD("Days Off In Period"),
    NUMBER_OF_PARTOFDAY("Number Of Nights And Days"),
    MAXIMUM_SENIOR_DAYS_IN_YEAR("Maximum Senior Days In Year"),
    SHIFT_LENGTH("Shift length"),
    NUMBER_OF_SHIFTS_IN_INTERVAL("Shifts in Interval"),
    TIME_BANK("Time Bank"),
    VETO_PER_PERIOD("Veto per period"),
    /*CONSECUTIVE_NIGHTS_AND_DAYS("Consecutive Nights and days"),*/
    DAILY_RESTING_TIME("Daily Resting Time"),
    DURATION_BETWEEN_SHIFTS("Duration Between Shifts"),
    REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS("Rest In Consecutive Days and nights"),
    WEEKLY_REST_PERIOD("Weekly Rest Period"),
    NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD("Number Of Weekend Shift In Period"),
    SHORTEST_AND_AVERAGE_DAILY_REST("Shortest And Average Daily Rest");

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
