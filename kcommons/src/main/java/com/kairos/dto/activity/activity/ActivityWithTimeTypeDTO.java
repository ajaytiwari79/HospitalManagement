package com.kairos.dto.activity.activity;

import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;

import java.util.List;

public class ActivityWithTimeTypeDTO {
    private List<ActivityDTO> activityDTOS;
    private List<TimeTypeDTO> timeTypeDTOS;
    private List<OpenShiftIntervalDTO> intervals;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters;


    public ActivityWithTimeTypeDTO() {
        //Default Constructor
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS, List<OpenShiftIntervalDTO> intervals,List<CounterDTO> counters) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
        this.intervals = intervals;
        this.counters=counters;
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS, List<OpenShiftIntervalDTO> intervals, Integer minOpenShiftHours,List<CounterDTO> counters) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
        this.intervals = intervals;
        this.minOpenShiftHours = minOpenShiftHours;
        this.counters=counters;
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

    public List<CounterDTO> getCounters() {
        return counters;
    }

    public void setCounters(List<CounterDTO> counters) {
        this.counters = counters;
    }
}
