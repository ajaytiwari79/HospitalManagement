package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffIncludeFilter {

   // private StaffWTARuleFilter staffWTARuleFilter;
    private boolean allowForFullTimeEmployees;
    private boolean allowForPartTimeEmployees;
    private boolean allowForHourlyPaidEmployees;
    private boolean allowForVolunteers;
    private boolean allowForFlexPool;
    private List<BigInteger> skillIds;
    private List<BigInteger> expertiseIds;

    public StaffIncludeFilter() {
        //Default Constructor
    }

    public StaffIncludeFilter(boolean allowForFullTimeEmployees, boolean allowForPartTimeEmployees,
                              boolean allowForHourlyPaidEmployees, boolean allowForVolunteers, boolean allowForFlexPool, List<BigInteger> skillIds, List<BigInteger> expertiseIds) {
        this.allowForFullTimeEmployees = allowForFullTimeEmployees;
        this.allowForPartTimeEmployees = allowForPartTimeEmployees;
        this.allowForHourlyPaidEmployees = allowForHourlyPaidEmployees;
        this.allowForVolunteers = allowForVolunteers;
        this.allowForFlexPool = allowForFlexPool;
        this.skillIds = skillIds;
        this.expertiseIds = expertiseIds;
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

    public List<BigInteger> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<BigInteger> skillIds) {
        this.skillIds = skillIds;
    }

    public List<BigInteger> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(List<BigInteger> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }
}
