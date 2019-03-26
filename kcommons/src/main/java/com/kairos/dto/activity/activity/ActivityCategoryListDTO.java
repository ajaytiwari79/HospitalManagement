package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityCategoryListDTO {
    private ActivityCategoryDTO activityCategory;
    private List<ActivityDTO> activities;

    public ActivityCategoryListDTO(ActivityCategoryDTO activityCategory, List<ActivityDTO> activities) {
        this.activityCategory = activityCategory;
        this.activities = activities;
    }

    public ActivityCategoryDTO getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(ActivityCategoryDTO activityCategory) {
        this.activityCategory = activityCategory;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
