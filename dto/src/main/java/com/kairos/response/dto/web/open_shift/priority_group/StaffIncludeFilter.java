package com.kairos.response.dto.web.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffIncludeFilter {

   // private StaffWTARuleFilter staffWTARuleFilter;
    private boolean allowForFullTimeEmployees;
    private boolean allowForPartTimeEmployees;
    private boolean allowForHourlyPaidEmployees;
    private boolean allowForVolunteers;
    private boolean allowForFlexPool;
    private List<Long> expertiseIds;
    private boolean fullAvailability; //In Minutes
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter
    private List<Long> employmentTypeIds;


    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }


    public StaffIncludeFilter() {
        //Default Constructor
    }

    public StaffIncludeFilter(boolean allowForFullTimeEmployees, boolean allowForPartTimeEmployees, boolean allowForHourlyPaidEmployees,
                              boolean allowForVolunteers, boolean allowForFlexPool, List<Long> expertiseIds, boolean fullAvailability, Float staffAvailability, Integer distanceFromUnit) {
        this.allowForFullTimeEmployees = allowForFullTimeEmployees;
        this.allowForPartTimeEmployees = allowForPartTimeEmployees;
        this.allowForHourlyPaidEmployees = allowForHourlyPaidEmployees;
        this.allowForVolunteers = allowForVolunteers;
        this.allowForFlexPool = allowForFlexPool;
        this.expertiseIds = expertiseIds;
        this.fullAvailability = fullAvailability;
        this.staffAvailability=staffAvailability;
        this.distanceFromUnit=distanceFromUnit;
    }

    public boolean isAllowForFullTimeEmployees() {
        return allowForFullTimeEmployees;
    }

    public void setAllowForFullTimeEmployees(boolean allowForFullTimeEmployees) {
        this.allowForFullTimeEmployees = allowForFullTimeEmployees;
    }

    public boolean isAllowForPartTimeEmployees() {
        return allowForPartTimeEmployees;
    }

    public void setAllowForPartTimeEmployees(boolean allowForPartTimeEmployees) {
        this.allowForPartTimeEmployees = allowForPartTimeEmployees;
    }

    public boolean isAllowForHourlyPaidEmployees() {
        return allowForHourlyPaidEmployees;
    }

    public void setAllowForHourlyPaidEmployees(boolean allowForHourlyPaidEmployees) {
        this.allowForHourlyPaidEmployees = allowForHourlyPaidEmployees;
    }

    public boolean isAllowForVolunteers() {
        return allowForVolunteers;
    }

    public void setAllowForVolunteers(boolean allowForVolunteers) {
        this.allowForVolunteers = allowForVolunteers;
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

    public boolean isFullAvailability() {
        return fullAvailability;
    }

    public void setFullAvailability(boolean fullAvailability) {
        this.fullAvailability = fullAvailability;
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
