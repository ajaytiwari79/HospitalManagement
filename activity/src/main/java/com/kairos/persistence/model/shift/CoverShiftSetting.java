package com.kairos.persistence.model.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class CoverShiftSetting {
    private Long unitId;
    private Long countryId;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Short maxLimitPendingRequest;//Staffs with more than x(input field) pending invitations are not eligible : default value is 5
    private Set<CoverShiftCriteria> coverShiftCriteria;

    public enum CoverShiftCriteria {
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
}
