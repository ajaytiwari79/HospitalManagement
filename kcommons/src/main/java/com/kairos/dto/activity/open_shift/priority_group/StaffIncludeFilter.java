package com.kairos.dto.activity.open_shift.priority_group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffIncludeFilter {

    private boolean allowForFlexPool;
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter

    public StaffIncludeFilter() {
        //Default Constructor
    }

    public StaffIncludeFilter(boolean allowForFlexPool,  Float staffAvailability, Integer distanceFromUnit) {
        this.allowForFlexPool = allowForFlexPool;
        this.staffAvailability=staffAvailability;
        this.distanceFromUnit=distanceFromUnit;
    }

    public boolean isAllowForFlexPool() {
        return allowForFlexPool;
    }

    public void setAllowForFlexPool(boolean allowForFlexPool) {
        this.allowForFlexPool = allowForFlexPool;
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
