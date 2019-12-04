package com.kairos.enums.kpi;

public enum CalculationType {
    SCHEDULED_HOURS("Scheduled Hours"), PLANNED_HOURS_TIMEBANK("Planned Hours of Timebank"),DURATION_HOURS("Duration"),TOTAL_MINUTES("Total Minutes"),COLLECTIVE_TIME_BONUS_TIMEBANK("Collective time bonus of timebank"),
    PAYOUT("Planned Hours of Payout"),COLLECTIVE_TIME_BONUS_PAYOUT("Collective time bonus of payout"),TOTAL_COLLECTIVE_BONUS("Total Collective Bonus"),TOTAL_PLANNED_HOURS("Total Planned Hours"),DELTA_TIMEBANK("Delta Timebank"),UNAVAILABILITY("Unavailability"),STAFFING_LEVEL_CAPACITY("Staffing Level capacity"), TOTAL_ABSENCE_DAYS("Total absence days left") , SENIORDAYS("Senior days left"),CHILD_CARE_DAYS("Childcare days left"),CARE_DAYS("Vacations days left "),PROTECTED_DAYS_OFF("protected days off");;

    public String value;

    CalculationType(String value) {
        this.value = value;
    }

}
