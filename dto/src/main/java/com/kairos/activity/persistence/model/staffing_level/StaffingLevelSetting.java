package com.kairos.activity.persistence.model.staffing_level;

import java.math.BigInteger;

public class StaffingLevelSetting {
    private int defaultDetailLevelMinutes=15;
    private Integer detailLevelMinutes;
    private StaffingLevelDuration duration;

    public StaffingLevelSetting() {
        //default constructor
    }

    public StaffingLevelSetting(Integer detailLevelMinutes, StaffingLevelDuration duration) {
        this.detailLevelMinutes = detailLevelMinutes;
        this.duration = duration;
    }

    public Integer getDetailLevelMinutes() {
        return detailLevelMinutes;
    }

    public void setDetailLevelMinutes(Integer detailLevelMinutes) {
        this.detailLevelMinutes = detailLevelMinutes;
    }

    public StaffingLevelDuration getDuration() {
        return duration;
    }

    public void setDuration(StaffingLevelDuration duration) {
        this.duration = duration;
    }

    public int getDefaultDetailLevelMinutes() {
        return defaultDetailLevelMinutes;
    }

    public void setDefaultDetailLevelMinutes(int defaultDetailLevelMinutes) {
        this.defaultDetailLevelMinutes = defaultDetailLevelMinutes;
    }
}
