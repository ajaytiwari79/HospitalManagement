package com.kairos.enums;

/**
 * Created by prerna on 30/4/18.
 * Modified By: mohit.shakya@oodlestechnologies.com on Jun 26th, 2018
 */

public enum FilterType {

    EMPLOYMENT_TYPE("Employment Type"), EXPERTISE("Expertise"), STAFF_STATUS("Status"), GENDER("Gender"),
    TIME_TYPE("Time Type"), PLANNED_TIME_TYPE("Planned Time Type"), ACTIVITY_CATEGORY_TYPE("Category Type"), ORGANIZATION_TYPE("Organization Type"),
    STAFF_IDS("Staff"), ACTIVITY_IDS("Activity"), UNIT_IDS("Unit"), TIME_INTERVAL("Time Interval"),EMPLOYMENT("Employment"), SELECTED_STAFF_IDS("Selected Staff IDs"),
    SKILLS("Skills"),REAL_TIME_STATUS("Real Time Status"),TAGS("Tags"),GROUPS("Groups"),NIGHT_WORKERS("Night Workers"),
    ACTIVITY_STATUS("Activity Status"),PHASE("Phase"),DAYS_OF_WEEK("Days Of Week"),DAY_TYPE("Day Type"),TIME_SLOT("Time Slot"),FIBONACCI("Fibonacci"),UNIT_NAME("Unit Name"),ACTIVITY_TIMECALCULATION_TYPE("Activity Timecalculation Type"),TEAM("Team"),
    ABSENCE_ACTIVITY("Absence Activity"),FUNCTIONS("Functions"),VALIDATED_BY("Validated By"),CALCULATION_TYPE("Calculation type"),CALCULATION_BASED_ON("Calculation Based On"),CALCULATION_UNIT("Calculation Unit"),PLANNED_BY("Planned By"),REASON_CODE("Reason Code"),
    AGE("Age"),ORGANIZATION_EXPERIENCE("Organisation Experience"),ESCALATION_CAUSED_BY("Escalation Caused By"),EMPLOYMENT_SUB_TYPE("Employment Sub Type"),
    MAIN_TEAM("Main Team"),SKILL_LEVEL("Skill Level"),ACCESS_GROUPS("Access Groups"),BIRTHDAY("Birthday"),SENIORITY("Seniority"),PAY_GRADE_LEVEL("Pay Grade Level"),
    TIME_BANK_BALANCE("Time Bank balance"),EMPLOYED_SINCE("Employed Since"),TEAM_TYPE("Team Type"),CTA_ACCOUNT_TYPE("CTA Account Type"),ASSIGN_ACTIVITY("Assign Activity"),ASSIGN_TIME_TYPE("Assign Time Type");


    public String value;

    FilterType(String value) {
        this.value = value;
    }

}