package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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


}
