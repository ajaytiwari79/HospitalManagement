package com.kairos.dto.activity.flexible_time;/*
 *Created By Pavan on 20/10/18
 *
 */

import java.math.BigInteger;

public class FlexibleTimeSettingsDTO {
    private BigInteger id;
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;
    private Long countryId;
    private Long unitId;
    private BigInteger activityId;

    public FlexibleTimeSettingsDTO() {
        //Default Constructor
    }

    public FlexibleTimeSettingsDTO(BigInteger id, FlexibleTimeDetails flexibleTimeForCheckIn, FlexibleTimeDetails flexibleTimeForCheckOut, Long countryId, Long unitId, BigInteger activityId) {
        this.id = id;
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
        this.countryId = countryId;
        this.unitId = unitId;
        this.activityId = activityId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public FlexibleTimeDetails getFlexibleTimeForCheckIn() {
        return flexibleTimeForCheckIn;
    }

    public void setFlexibleTimeForCheckIn(FlexibleTimeDetails flexibleTimeForCheckIn) {
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
    }

    public FlexibleTimeDetails getFlexibleTimeForCheckOut() {
        return flexibleTimeForCheckOut;
    }

    public void setFlexibleTimeForCheckOut(FlexibleTimeDetails flexibleTimeForCheckOut) {
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }
}
