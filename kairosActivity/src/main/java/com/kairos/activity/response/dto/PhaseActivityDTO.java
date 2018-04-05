package com.kairos.activity.response.dto;

import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.Phase.PhaseWeeklyDTO;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 19/9/17.
 */
public class PhaseActivityDTO {
    private List<ActivityWithCompositeDTO> activities;
    private List<PhaseWeeklyDTO> phases;
    private List<DayType> dayTypes= new ArrayList<>();

    public PhaseActivityDTO() {
        //Default Constructor
    }

    private List<PhaseDTO> applicablePhases;


    public List<ActivityWithCompositeDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityWithCompositeDTO> activities) {
        this.activities = activities;
    }

    public List<PhaseWeeklyDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseWeeklyDTO> phases) {
        this.phases = phases;
    }

    public List<PhaseDTO> getApplicablePhases() {
        return applicablePhases;
    }

    public void setApplicablePhases(List<PhaseDTO> applicablePhases) {
        this.applicablePhases = applicablePhases;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
