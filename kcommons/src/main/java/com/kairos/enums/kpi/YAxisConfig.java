package com.kairos.enums.kpi;

public enum YAxisConfig {

    TIME_TYPE("TimeType"),ACTIVITY("Activity"),PLANNED_TIME("Planned Time"),TOTAL_PLANNED_HOURS("Total Planned Hours"),DELTA_TIMEBANK("Delta Timebank"),UNAVAILABILITY("Unavailability"),PLANNING_QUALITY_LEVEL("Planning Quality Level");

    public String value;

    YAxisConfig(String value) {
        this.value = value;
    }
}
