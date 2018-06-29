package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 25/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationMappingActivityDTO {
    private BigInteger activityId;
    private List<Long> expertises = new ArrayList<>();
    private List<Long> organizationTypes = new ArrayList<>();
    private List<Long> organizationSubTypes = new ArrayList<>();
    private List<Long> regions = new ArrayList<>();
    private List<Long> level = new ArrayList<>();
    private List<Long> employmentTypes = new ArrayList<>();

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<Long> getRegions() {
        return regions;
    }

    public void setRegions(List<Long> regions) {
        this.regions = regions;
    }

    public List<Long> getLevel() {
        return level;
    }

    public void setLevel(List<Long> level) {
        this.level = level;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
