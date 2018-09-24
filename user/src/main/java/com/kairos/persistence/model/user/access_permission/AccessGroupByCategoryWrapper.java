package com.kairos.persistence.model.user.access_permission;/*
 *Created By Pavan on 24/9/18
 *
 */

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;

import java.util.List;

public class AccessGroupByCategoryWrapper {
    private List<AccessGroupsByCategoryDTO> accessGroups;
    private List<DayTypeDTO> dayTypes;

    public AccessGroupByCategoryWrapper() {
        //Default Constructor
    }

    public AccessGroupByCategoryWrapper(List<AccessGroupsByCategoryDTO> accessGroups, List<DayTypeDTO> dayTypes) {
        this.accessGroups = accessGroups;
        this.dayTypes = dayTypes;
    }

    public List<AccessGroupsByCategoryDTO> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroupsByCategoryDTO> accessGroups) {
        this.accessGroups = accessGroups;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
