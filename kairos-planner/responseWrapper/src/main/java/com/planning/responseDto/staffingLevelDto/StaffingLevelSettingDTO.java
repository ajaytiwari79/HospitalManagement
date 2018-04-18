package com.planning.responseDto.staffingLevelDto;

public class StaffingLevelSettingDTO {
    private int defaultDetailLevelMinutes=15;
    private Integer detailLevelMinutes;
    private StaffingLevelDurationDTO duration;

    public StaffingLevelSettingDTO() {
        //default constructor
    }

    public StaffingLevelSettingDTO(Integer detailLevelMinutes, StaffingLevelDurationDTO duration) {
        this.detailLevelMinutes = detailLevelMinutes;
        this.duration = duration;
    }

    public Integer getDetailLevelMinutes() {
        return detailLevelMinutes;
    }

    public void setDetailLevelMinutes(Integer detailLevelMinutes) {
        this.detailLevelMinutes = detailLevelMinutes;
    }

    public StaffingLevelDurationDTO getDuration() {
        return duration;
    }

    public void setDuration(StaffingLevelDurationDTO duration) {
        this.duration = duration;
    }

    public int getDefaultDetailLevelMinutes() {
        return defaultDetailLevelMinutes;
    }

    public void setDefaultDetailLevelMinutes(int defaultDetailLevelMinutes) {
        this.defaultDetailLevelMinutes = defaultDetailLevelMinutes;
    }
}
