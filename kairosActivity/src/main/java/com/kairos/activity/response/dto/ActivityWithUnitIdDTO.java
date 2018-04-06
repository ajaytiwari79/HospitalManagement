package com.kairos.activity.response.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;

import java.util.List;

/**
 * Created by vipul on 4/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityWithUnitIdDTO {
    private Long unitId;
    private List<ActivityTagDTO> activityDTOList;
    private List<Activity> activities;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public ActivityWithUnitIdDTO() {
    }

    public List<ActivityTagDTO> getActivityDTOList() {
        return activityDTOList;
    }

    public void setActivityDTOList(List<ActivityTagDTO> activityDTOList) {
        this.activityDTOList = activityDTOList;
    }

    public ActivityWithUnitIdDTO(Long unitId, List<ActivityTagDTO> activityDTOList) {
        this.unitId = unitId;
        this.activityDTOList = activityDTOList;
    }
}
