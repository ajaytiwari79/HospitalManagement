package com.kairos.response.dto.web;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;


import java.util.List;

public class ActivityWithTimeTypeDTO {
    private List<ActivityDTO> activityDTOS;
    private List<TimeTypeDTO> timeTypeDTOS;
    private List<OpenShiftIntervalDTO> intervals;


    public ActivityWithTimeTypeDTO() {
        //Default Constructor
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS, List<OpenShiftIntervalDTO> intervals) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
        this.intervals = intervals;
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
}
