package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;

public class TimeAttendanceGracePeriod extends MongoBaseEntity {

    private Long unitId;
    private int staffGracePeriodDays;
    private int managementGracePeriodDays;

    public TimeAttendanceGracePeriod() {
    }

    public TimeAttendanceGracePeriod(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getStaffGracePeriodDays() {
        return staffGracePeriodDays;
    }

    public void setStaffGracePeriodDays(int staffGracePeriodDays) {
        this.staffGracePeriodDays = staffGracePeriodDays;
    }

    public int getManagementGracePeriodDays() {
        return managementGracePeriodDays;
    }

    public void setManagementGracePeriodDays(int managementGracePeriodDays) {
        this.managementGracePeriodDays = managementGracePeriodDays;
    }
}
