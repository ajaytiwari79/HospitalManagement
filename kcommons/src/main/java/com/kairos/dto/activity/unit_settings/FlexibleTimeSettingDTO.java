package com.kairos.dto.activity.unit_settings;
/*
 *Created By Pavan on 10/9/18
 *
 */

public class FlexibleTimeSettingDTO {
    private Short checkInFlexibleTime;
    private Short checkOutFlexibleTime;
    private Long unitId;

    public FlexibleTimeSettingDTO() {
        //Default Constructor
    }

    public Short getCheckInFlexibleTime() {
        return checkInFlexibleTime;
    }

    public void setCheckInFlexibleTime(Short checkInFlexibleTime) {
        this.checkInFlexibleTime = checkInFlexibleTime;
    }

    public Short getCheckOutFlexibleTime() {
        return checkOutFlexibleTime;
    }

    public void setCheckOutFlexibleTime(Short checkOutFlexibleTime) {
        this.checkOutFlexibleTime = checkOutFlexibleTime;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
