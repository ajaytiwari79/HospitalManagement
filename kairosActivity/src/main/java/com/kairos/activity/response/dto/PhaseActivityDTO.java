package com.kairos.activity.response.dto;

import com.kairos.response.dto.web.day_type.DayType;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.Phase.PhaseWeeklyDTO;
import com.kairos.response.dto.web.access_group.UserAccessRoleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 19/9/17.
 */
public class PhaseActivityDTO {
    private List<ActivityWithCompositeDTO> activities;
    private List<PhaseWeeklyDTO> phases;
    private List<DayType> dayTypes= new ArrayList<>();
    private UserAccessRoleDTO staffAccessRole;

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

    public UserAccessRoleDTO getStaffAccessRole() {
        return staffAccessRole;
    }

    public void setStaffAccessRole(UserAccessRoleDTO staffAccessRole) {
        this.staffAccessRole = staffAccessRole;
    }
}
