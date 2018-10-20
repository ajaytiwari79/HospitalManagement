package com.kairos.persistence.model.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class FlexibleTimeSettings extends MongoBaseEntity {
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;
    private Long countryId;
    private Long unitId;
    private BigInteger activityId;

    public FlexibleTimeSettings() {
        //Default Constructor
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
