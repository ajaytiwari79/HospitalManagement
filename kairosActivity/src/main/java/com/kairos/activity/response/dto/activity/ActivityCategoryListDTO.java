package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.activity.response.dto.ActivityDTO;

import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityCategoryListDTO {
    private ActivityCategory activityCategory;
    private List<ActivityTagDTO> activities =new ArrayList<>();

    public ActivityCategoryListDTO(ActivityCategory activityCategory, List<ActivityTagDTO> activities) {
        this.activityCategory = activityCategory;
        this.activities = activities;
    }

    public ActivityCategory getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(ActivityCategory activityCategory) {
        this.activityCategory = activityCategory;
    }

    public List<ActivityTagDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityTagDTO> activities) {
        this.activities = activities;
    }
}
