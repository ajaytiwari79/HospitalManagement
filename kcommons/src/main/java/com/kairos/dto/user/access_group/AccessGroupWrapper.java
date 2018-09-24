package com.kairos.dto.user.access_group;/*
 *Created By Pavan on 24/9/18
 *
 */

import com.kairos.dto.user.country.agreement.cta.cta_response.AccessGroupDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;

import java.util.List;

public class AccessGroupWrapper {
    private List<AccessGroupDTO> accessGroups;
    private List<DayTypeDTO> dayTypes;

    public AccessGroupWrapper() {
        //Default Constructor
    }

    public AccessGroupWrapper(List<AccessGroupDTO> accessGroups, List<DayTypeDTO> dayTypes) {
        this.accessGroups = accessGroups;
        this.dayTypes = dayTypes;
    }

    public List<AccessGroupDTO> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroupDTO> accessGroups) {
        this.accessGroups = accessGroups;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
