package com.kairos.activity.activity;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 5/9/17.
 */
public class OrganizationMappingActivityTypeDTO {
    private BigInteger activityTypeId;
    private List<Long> expertises;
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private List<Long> regions;
    private List<Long> level;

    public BigInteger getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(BigInteger activityTypeId) {
        this.activityTypeId = activityTypeId;
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
}
