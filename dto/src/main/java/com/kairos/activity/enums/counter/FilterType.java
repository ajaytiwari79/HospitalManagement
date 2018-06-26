package com.kairos.activity.enums.counter;

import java.util.List;

/**
 * Created by prerna on 30/4/18.
 */
public enum FilterType {

    EMPLOYMENT_TYPE(0, "Employment Type"),
    EXPERTISE_TYPE(1, "Expertise"),
    STAFF_STATUS(2, "Status"),
    GENDER(3, "Gender"),
    ENGINEER_TYPE(4, "Engineer Type"),
    TIME_TYPE(5, "Time Type"),
    PLANNED_TIME_TYPE(6, "Planned Time Type"),
    ACTIVITY_CATEGORY_TYPE(7, "Category Type"),
    ORGANIZATION_TYPE(8, "Organization Type"),
    STAFF_IDS(9, "Staff"),
    ACTIVITY_IDS(10, "Activity"),
    UNIT_IDS(11, "Unit"),
    TIME_INTERVAL(12, "Time Interval");

    private String value;
    private int order;

    FilterType(int order, String value) {
        this.order = order;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public int getOrder(){
        return order;
    }
}
