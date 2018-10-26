package com.kairos.dto.activity.staffing_level;

import java.math.BigInteger;
import java.util.Map;

public class StaffingLevelSetting {
    private int defaultDetailLevelMinutes=15;
    private Integer detailLevelMinutes;
    private Duration duration;
    private Map<BigInteger,Integer> activitiesRank;

    public StaffingLevelSetting() {
        //default constructor
    }

    public StaffingLevelSetting(Integer detailLevelMinutes, Duration duration) {
        this.detailLevelMinutes = detailLevelMinutes;
        this.duration = duration;
    }

    public Integer getDetailLevelMinutes() {
        return detailLevelMinutes;
    }

    public void setDetailLevelMinutes(Integer detailLevelMinutes) {
        this.detailLevelMinutes = detailLevelMinutes;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getDefaultDetailLevelMinutes() {
        return defaultDetailLevelMinutes;
    }

    public void setDefaultDetailLevelMinutes(int defaultDetailLevelMinutes) {
        this.defaultDetailLevelMinutes = defaultDetailLevelMinutes;
    }

    public Map<BigInteger, Integer> getActivitiesRank() {
        return activitiesRank;
    }

    public void setActivitiesRank(Map<BigInteger, Integer> activitiesRank) {
        this.activitiesRank = activitiesRank;
    }
}
