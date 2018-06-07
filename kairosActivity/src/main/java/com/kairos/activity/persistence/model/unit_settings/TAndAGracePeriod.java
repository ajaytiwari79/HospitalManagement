package com.kairos.activity.persistence.model.unit_settings;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class TAndAGracePeriod extends MongoBaseEntity {

    private Long unitId;
    private int staffGracePeriodDays;
    private int managementGracePeriodDays;

    public TAndAGracePeriod() {
    }

    public TAndAGracePeriod(Long unitId, int staffGracePeriodDays, int managementGracePeriodDays) {
        this.unitId = unitId;
        this.staffGracePeriodDays = staffGracePeriodDays;
        this.managementGracePeriodDays = managementGracePeriodDays;
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
