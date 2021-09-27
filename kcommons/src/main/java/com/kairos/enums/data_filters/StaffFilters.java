package com.kairos.enums.data_filters;

import java.io.Serializable;

public enum StaffFilters implements Serializable {

    EMPLOYMENT_TYPE("Employment Type"), EXPERTISE("Expertise"), STAFF_STATUS("Staff Status"), GENDER("Gender"),
    ORGANIZATION_TYPE("Organization Type"),
    STAFF_IDS("Staff"), ACTIVITY_IDS("Activity"), UNIT_IDS("Unit"), TIME_INTERVAL("Time Interval"),EMPLOYMENT("Employment"), SELECTED_STAFF_IDS("Selected Staff IDs"),
    SKILLS("Skills"),REAL_TIME_STATUS("Real Time Status"),TAGS("Tags"),GROUPS("Groups"),NIGHT_WORKERS("Night Workers"),
    DAYS_OF_WEEK("Days Of Week"),DAY_TYPE("Day Type"),TIME_SLOT("Time Slot"),FIBONACCI("Fibonacci"),UNIT_NAME("Unit Name"),ACTIVITY_TIMECALCULATION_TYPE("Activity Timecalculation Type"),TEAM("Team"),
    FUNCTIONS("Functions"),VALIDATED_BY("Validated By"),CALCULATION_TYPE("Calculation type"),CALCULATION_BASED_ON("Calculation Based On"),CALCULATION_UNIT("Calculation Unit"),PLANNED_BY("Planned By"),REASON_CODE("Reason Code"),
    AGE("Age"),ORGANIZATION_EXPERIENCE("Organisation Experience"),EMPLOYMENT_SUB_TYPE("Employment Sub Type"),
    MAIN_TEAM("Main Team"),SKILL_LEVEL("Skill Level"),ACCESS_GROUPS("Access Groups"),BIRTHDAY("Birthday"),SENIORITY("Seniority"),PAY_GRADE_LEVEL("Pay Grade Level"),CTA_ACCOUNT_TYPE("CTA account type"),
    TIME_BANK_BALANCE("Time Bank balance"),EMPLOYED_SINCE("Employed Since"),TEAM_TYPE("Team Type");


    public String value;

    StaffFilters(String value) {
        this.value = value;
    }
}
