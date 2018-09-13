package com.kairos.persistence.model.unit_settings;
/*
 *Created By Pavan on 7/9/18
 *Used to Configure the Flexible time settings per unit
 */

public class FlexibleTimeSettings {
    private Short checkInFlexibleTime;
    private Short checkOutFlexibleTime;

    public FlexibleTimeSettings() {
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
}
