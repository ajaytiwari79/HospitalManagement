package com.kairos.enums.shift;

import java.io.Serializable;

public enum CoverShiftCriteria implements Serializable {
    STAFF_WITH_FREE_DAYS,
    STAFF_WITH_MAX_PENDING_INVITATIONS,
    STAFF_WITH_PERSONAL_CALENDAR,
    STAFF_WITH_WTA_RULE_VIOLATION,
    STAFF_WITH_SICKNESS,
    STAFF_WITH_TAGS,
    STAFF_WITH_PENDING_ABSENCE_REQUEST,
    STAFF_WITH_PLANNED_STOP_BRICKS,
    STAFF_WITH_PLANNED_VETO,
    STAFF_WITH_PLANNED_PROTECTED_DAYS_OFF,
    STAFF_WITH_PLANNED_UNAVAILABLE,
    STAFF_WITH_ASSIGNED_SAME_FUNCTIONS,
    STAFF_WITH_EMPLOYEMENT_TYPES
}
