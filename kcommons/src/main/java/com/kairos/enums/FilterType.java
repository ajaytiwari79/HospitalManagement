package com.kairos.enums;

import static com.kairos.enums.FilterType.MatchType.SHIFT;
import static com.kairos.enums.FilterType.MatchType.STAFF;

/**
 * Created by prerna on 30/4/18.
 * Modified By: mohit.shakya@oodlestechnologies.com on Jun 26th, 2018
 */

public enum FilterType {

    EMPLOYMENT_TYPE("Employment Type", STAFF), EXPERTISE("Expertise", STAFF), STAFF_STATUS("Status", STAFF), GENDER("Gender", STAFF),
    TIME_TYPE("Time Type", SHIFT), PLANNED_TIME_TYPE("Planned Time Type", SHIFT), ACTIVITY_CATEGORY_TYPE("Category Type", SHIFT), ORGANIZATION_TYPE("Organization Type", STAFF),
    STAFF_IDS("Staff", STAFF), ACTIVITY_IDS("Activity", SHIFT), UNIT_IDS("Unit", STAFF), TIME_INTERVAL("Time Interval", SHIFT), EMPLOYMENT("Employment", STAFF), SELECTED_STAFF_IDS("Selected Staff IDs", STAFF),
    SKILLS("Skills", STAFF), REAL_TIME_STATUS("Real Time Status", SHIFT), TAGS("Tags", STAFF), GROUPS("Groups", STAFF), NIGHT_WORKERS("Night Workers", STAFF),
    ACTIVITY_STATUS("Activity Status", SHIFT), PHASE("Phase", SHIFT), DAYS_OF_WEEK("Days Of Week", SHIFT), DAY_TYPE("Day Type", SHIFT), TIME_SLOT("Time Slot", SHIFT), FIBONACCI("Fibonacci", SHIFT), UNIT_NAME("Unit Name", STAFF), ACTIVITY_TIMECALCULATION_TYPE("Activity Timecalculation Type", SHIFT), TEAM("Team", STAFF),
    ABSENCE_ACTIVITY("Absence Activity", SHIFT), FUNCTIONS("Functions", STAFF), VALIDATED_BY("Validated By", SHIFT), CALCULATION_TYPE("Calculation type", SHIFT), CALCULATION_BASED_ON("Calculation Based On", SHIFT), CALCULATION_UNIT("Calculation Unit", SHIFT), PLANNED_BY("Planned By", SHIFT), REASON_CODE("Reason Code", SHIFT),
    AGE("Age", STAFF), ORGANIZATION_EXPERIENCE("Organisation Experience", STAFF), ESCALATION_CAUSED_BY("Escalation Caused By", SHIFT), EMPLOYMENT_SUB_TYPE("Employment Sub Type", STAFF),
    MAIN_TEAM("Main Team", STAFF), SKILL_LEVEL("Skill Level", STAFF), ACCESS_GROUPS("Access Groups", STAFF), BIRTHDAY("Birthday", STAFF), SENIORITY("Seniority", STAFF), PAY_GRADE_LEVEL("Pay Grade Level", STAFF),
    TIME_BANK_BALANCE("Time Bank balance", SHIFT), EMPLOYED_SINCE("Employed Since", STAFF), TEAM_TYPE("Team Type", STAFF), CTA_ACCOUNT_TYPE("CTA Account Type", STAFF), ASSIGN_ACTIVITY("Assign Activity", STAFF), ASSIGN_TIME_TYPE("Assign Time Type", STAFF),
    UPDATED_DATA_AFTER_PLANNING_PERIOD_PUBLISH("Update Data After Planning Period Publish", SHIFT),INCLUDE_DRAFT_SHIFT("Include Draft Shift",SHIFT);


    private String value;
    private MatchType matchType;

    FilterType(String value, MatchType matchType) {
        this.value = value;
        this.matchType = matchType;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public String getValue() {
        return value;
    }

    public enum MatchType {
        SHIFT, STAFF
    }

    public enum FilterComparisonType {
        CONTAINS, LESS_THAN, GREATER_THAN, BETWEEN, DUE_IN
    }
}