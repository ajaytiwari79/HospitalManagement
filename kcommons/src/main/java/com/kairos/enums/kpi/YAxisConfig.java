package com.kairos.enums.kpi;

public enum YAxisConfig {

    PRESENCE_UNDER_STAFFING("Presence Under Staffing"),PRESENCE_OVER_STAFFING("Presence Over Staffing"),ABSENCE_UNDER_STAFFING("Absence Under Staffing"),ABSENCE_OVER_STAFFING("Absence Over Staffing"),STAFF_AGE("Staff Age"),SUM_OF_CHILDREN("Sum Of Children"),WORKED_ON_PUBLIC_HOLIDAY("Worked On Public Holiday"),
    TIME_TYPE("TimeType"),ACTIVITY("Activity"),PLANNED_TIME("Planned Time"),TOTAL_PLANNED_HOURS("Total Planned Hours"),DELTA_TIMEBANK("Delta Timebank"),UNAVAILABILITY("Unavailability"), PAYOUT("Payout"),STAFFING_LEVEL_CAPACITY("Staffing Level capacity") ,
    TOTAL_ABSENCE_DAYS("Total absence days left") , SENIORDAYS("Senior days left"),CHILD_CARE_DAYS("Childcare days left"),CARE_DAYS("Vacations days left "),PROTECTED_DAYS_OFF("protected days off"),BREAK_INTERRUPT("Break Interrupt"),PLANNING_QUALITY_LEVEL("Planning Quality Level"),ESCALATED_SHIFTS("Escalated Shifts"),ESCALATION_RESOLVED_SHIFTS("Escalated Resolved ShiftS"),ACTUAL_TIMEBANK("Actual TimeBank"),ABSENCE_REQUEST("Absence Request"),STAFF_SKILLS_COUNT("Total Skill Count Of Staff"),TOTAL_WEEKLY_HOURS("Total Weekly Hours"),PAY_LEVEL_GRADE("PayLevel Grade");

    public String value;

    YAxisConfig(String value) {
        this.value = value;
    }
}
