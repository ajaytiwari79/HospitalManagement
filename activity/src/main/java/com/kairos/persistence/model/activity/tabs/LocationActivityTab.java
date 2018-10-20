package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.enums.LocationEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vipul on 13/4/18.
 */
public class LocationActivityTab implements Serializable {
    private List<LocationEnum> canBeStartAt;
    private List<LocationEnum> canBeEndAt;
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;

    public LocationActivityTab() {
        //Default Constructor
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

    public LocationActivityTab(List<LocationEnum> canBeStartAt, List<LocationEnum> canBeEndAt, FlexibleTimeDetails flexibleTimeForCheckIn, FlexibleTimeDetails flexibleTimeForCheckOut) {
        this.canBeStartAt = canBeStartAt;
        this.canBeEndAt = canBeEndAt;
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
    }
}
