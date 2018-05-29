package com.kairos.response.dto.web;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;


import java.util.List;

public class ActivityWithTimeTypeDTO {
    private List<ActivityDTO> activityDTOS;
    private List<TimeTypeDTO> timeTypeDTOS;


    public ActivityWithTimeTypeDTO() {
        //Default Constructor
    }

    public ActivityWithTimeTypeDTO(List<ActivityDTO> activityDTOS, List<TimeTypeDTO> timeTypeDTOS) {
        this.activityDTOS = activityDTOS;
        this.timeTypeDTOS = timeTypeDTOS;
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


}
