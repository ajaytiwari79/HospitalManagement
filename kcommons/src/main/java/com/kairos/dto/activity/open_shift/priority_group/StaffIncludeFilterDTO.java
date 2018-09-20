package com.kairos.dto.activity.open_shift.priority_group;

import java.time.LocalDate;
import java.util.List;

public class StaffIncludeFilterDTO {


    private boolean allowForFlexPool;
    private List<Long> expertiseIds;
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter
    private List<Long> employmentTypeIds;
    private LocalDate openShiftDate;
    private Long maxOpenShiftDate;

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

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public LocalDate getOpenShiftDate() {
        return openShiftDate;
    }

    public void setOpenShiftDate(LocalDate openShiftDate) {
        this.openShiftDate = openShiftDate;
    }
    public Long getMaxOpenShiftDate() {
        return maxOpenShiftDate;
    }

    public void setMaxOpenShiftDate(Long maxOpenShiftDate) {
        this.maxOpenShiftDate = maxOpenShiftDate;
    }

}
