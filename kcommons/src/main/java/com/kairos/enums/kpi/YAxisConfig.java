package com.kairos.enums.kpi;

public enum YAxisConfig {

    TIME_TYPE("TimeType"),ACTIVITY("Activity"),PLANNED_TIME("Planned Time"),TOTAL_PLANNED_HOURS("Total Planned Hours"),DELTA_TIMEBANK("Delta Timebank"),UNAVAILABILITY("Unavailability"), PAYOUT("Payout"),STAFFING_LEVEL_CAPACITY("Staffing Level capacity") , TOTAL_ABSENCE_DAYS("Total absence days left") , SENIORDAYS("Senior days left"),CHILD_CARE_DAYS("Childcare days left"),CARE_DAYS("Vacations days left "),PROTECTED_DAYS_OFF("protected days off");

    public String value;

    YAxisConfig(String value) {
        this.value = value;
    }
}
