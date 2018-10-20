package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.enums.LocationEnum;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 13/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationActivityTabDTO {
    private BigInteger activityId;
    private List<LocationEnum> canBeStartAt;
    private List<LocationEnum> canBeEndAt;
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;

    public LocationActivityTabDTO() {
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<LocationEnum> getCanBeStartAt() {
        return canBeStartAt;
    }

    public void setCanBeStartAt(List<LocationEnum> canBeStartAt) {
        this.canBeStartAt = canBeStartAt;
    }

    public List<LocationEnum> getCanBeEndAt() {
        return canBeEndAt;
    }

    public void setCanBeEndAt(List<LocationEnum> canBeEndAt) {
        this.canBeEndAt = canBeEndAt;
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
}
