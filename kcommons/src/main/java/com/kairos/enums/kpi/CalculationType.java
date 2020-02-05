package com.kairos.enums.kpi;

public enum CalculationType {
    SCHEDULED_HOURS("Scheduled Hours"), PLANNED_HOURS_TIMEBANK("Planned Hours of Timebank"),DURATION_HOURS("Duration"),TOTAL_MINUTES("Total Minutes"),COLLECTIVE_TIME_BONUS_TIMEBANK("Collective time bonus of timebank"),
    PRESENCE_UNDER_STAFFING("Presence Under Staffing"),PRESENCE_OVER_STAFFING("Presence Over Staffing"),ABSENCE_UNDER_STAFFING("Absence Under Staffing"),ABSENCE_OVER_STAFFING("Absence Over Staffing"),STAFF_AGE("Staff Age"),SUM_OF_CHILDREN("Sum Of Children"),WORKED_ON_PUBLIC_HOLIDAY("Worked On Public Holiday"),
    PAYOUT("Planned Hours of Payout"),COLLECTIVE_TIME_BONUS_PAYOUT("Collective time bonus of payout"),TOTAL_COLLECTIVE_BONUS("Total Collective Bonus"),TOTAL_PLANNED_HOURS("Total Planned Hours"),DELTA_TIMEBANK("Delta Timebank"),UNAVAILABILITY("Unavailability"),STAFFING_LEVEL_CAPACITY("Staffing Level capacity"), TOTAL_ABSENCE_DAYS("Total absence days left") , SENIORDAYS("Senior days left"),CHILD_CARE_DAYS("Childcare days left"),CARE_DAYS("Vacations days left "),PROTECTED_DAYS_OFF("protected days off"),BREAK_INTERRUPT("Break Interrupt"),ESCALATED_SHIFTS("Escalated Shifts"),ESCALATION_RESOLVED_SHIFTS("Escalated Resolved ShiftS"),ACTUAL_TIMEBANK("Actual TimeBank"),ABSENCE_REQUEST("Absence Request"),TOTAL_PRESENCE_DAYS("Total Presence Days"),STAFF_SKILLS_COUNT("Total Skill Count Of Staff"),TOTAL_WEEKLY_HOURS("Total weekly Hours");

    public String value;

    CalculationType(String value) {
        this.value = value;
    }

}
