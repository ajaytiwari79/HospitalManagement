package com.kairos.activity.activity;

import com.kairos.activity.counter.CounterDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.activity.open_shift.OpenShiftIntervalDTO;

import java.util.List;

public class ActivityWithTimeTypeDTO {
    private List<ActivityDTO> activityDTOS;
    private List<TimeTypeDTO> timeTypeDTOS;
    private List<OpenShiftIntervalDTO> intervals;
    private Integer minOpenShiftHours;


    public ActivityWithTimeTypeDTO() {
        //Default Constructor
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS, List<OpenShiftIntervalDTO> intervals) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
        this.intervals = intervals;
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS, List<OpenShiftIntervalDTO> intervals, Integer minOpenShiftHours) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
        this.intervals = intervals;
        this.minOpenShiftHours = minOpenShiftHours;
    }

    public List<TimeTypeDTO> getTimeTypeDTOS() {
        return timeTypeDTOS;
    }

    public void setTimeTypeDTOS(List<TimeTypeDTO> timeTypeDTOS) {
        this.timeTypeDTOS = timeTypeDTOS;
    }

    public List<ActivityDTO> getActivityDTOS() {
        return activityDTOS;
    }

    public void setActivityDTOS(List<ActivityDTO> activityDTOS) {
        this.activityDTOS = activityDTOS;
    }

    public List<OpenShiftIntervalDTO> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<OpenShiftIntervalDTO> intervals) {
        this.intervals = intervals;
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }

}
