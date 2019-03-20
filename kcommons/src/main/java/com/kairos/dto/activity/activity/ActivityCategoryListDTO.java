package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityCategoryListDTO {
    private ActivityCategoryDTO activityCategoryDTO;
    private List<ActivityDTO> activities;

    public ActivityCategoryListDTO(ActivityCategoryDTO activityCategoryDTO, List<ActivityDTO> activities) {
        this.activityCategoryDTO = activityCategoryDTO;
        this.activities = activities;
    }

    public ActivityCategoryDTO getActivityCategoryDTO() {
        return activityCategoryDTO;
    }

    public void setActivityCategoryDTO(ActivityCategoryDTO activityCategoryDTO) {
        this.activityCategoryDTO = activityCategoryDTO;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
