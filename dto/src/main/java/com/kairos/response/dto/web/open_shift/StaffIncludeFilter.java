package com.kairos.response.dto.web.open_shift;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class StaffIncludeFilter {

    private boolean allowForFlexPool;
    private List<Long> expertiseIds;
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter

    public StaffIncludeFilter() {
        //Default Constructor
    }

    public StaffIncludeFilter(boolean allowForFlexPool, List<Long> expertiseIds,  Float staffAvailability, Integer distanceFromUnit) {
        this.allowForFlexPool = allowForFlexPool;
        this.expertiseIds = expertiseIds;
        this.staffAvailability=staffAvailability;
        this.distanceFromUnit=distanceFromUnit;
    }

    public boolean isAllowForFlexPool() {
        return allowForFlexPool;
    }

    public void setAllowForFlexPool(boolean allowForFlexPool) {
        this.allowForFlexPool = allowForFlexPool;
    }

    public List<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(List<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public Float getStaffAvailability() {
        return staffAvailability;
    }

    public void setStaffAvailability(Float staffAvailability) {
        this.staffAvailability = staffAvailability;
    }

    public Integer getDistanceFromUnit() {
        return distanceFromUnit;
    }

    public void setDistanceFromUnit(Integer distanceFromUnit) {
        this.distanceFromUnit = distanceFromUnit;
    }
}
