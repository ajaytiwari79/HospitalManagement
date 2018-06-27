package com.kairos.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by vipul on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityWithSelectedDTO {
    List<ActivityTagDTO> allActivities;
    List<ActivityTagDTO> selectedActivities;

    public ActivityWithSelectedDTO(List<ActivityTagDTO> allActivities, List<ActivityTagDTO> selectedActivities) {
        this.allActivities = allActivities;
        this.selectedActivities = selectedActivities;
    }

    public ActivityWithSelectedDTO() {

    }

    public List<ActivityTagDTO> getAllActivities() {
        return allActivities;
    }

    public void setAllActivities(List<ActivityTagDTO> allActivities) {
        this.allActivities = allActivities;
    }

    public List<ActivityTagDTO> getSelectedActivities() {
        return selectedActivities;
    }

    public void setSelectedActivities(List<ActivityTagDTO> selectedActivities) {
        this.selectedActivities = selectedActivities;
    }
}
