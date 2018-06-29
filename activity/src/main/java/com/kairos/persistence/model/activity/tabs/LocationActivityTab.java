package com.kairos.persistence.model.activity.tabs;

import com.kairos.enums.LocationEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vipul on 13/4/18.
 */
public class LocationActivityTab implements Serializable {
    private List<LocationEnum> canBeStartAt;
    private List<LocationEnum> canBeEndAt;

    public LocationActivityTab() {
        //dc
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

    public LocationActivityTab(List<LocationEnum> canBeStartAt, List<LocationEnum> canBeEndAt) {
        this.canBeStartAt = canBeStartAt;
        this.canBeEndAt = canBeEndAt;
    }
}
