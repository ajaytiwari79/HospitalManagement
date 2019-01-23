package com.kairos.enums;

/**
 * Created by prerna on 30/4/18.
 * Modified By: mohit.shakya@oodlestechnologies.com on Jun 26th, 2018
 */

public enum FilterType {

    EMPLOYMENT_TYPE("Employment Type"), EXPERTISE("Expertise"), STAFF_STATUS("Status"), GENDER("Gender"), ENGINEER_TYPE("Engineer Type"),
    TIME_TYPE("Time Type"), PLANNED_TIME_TYPE("Planned Time Type"), ACTIVITY_CATEGORY_TYPE("Category Type"), ORGANIZATION_TYPE("Organization Type"),
    STAFF_IDS("Staff Ids"), ACTIVITY_IDS("Activity"), UNIT_IDS("Unit"), TIME_INTERVAL("Time Interval"),UNIT_POSITION("Unit Position"), SELECTED_STAFF_IDS("Selected Staff IDs"),
    APPROVAL_STATUS("Approval Status"),PHASE("phase"),DAYS_OF_WEEK("days of week"),DAY_TYPE("day type"),TIME_SLOT("time slot");

    public String value;

    FilterType(String value) {
        this.value = value;
    }
}
