package com.kairos.persistence.model.activity.tabs;

import java.util.List;

/**
 * Created by vipul on 25/8/17.
 */
public class OrganizationMappingActivity {

    List<Long> expertize;
    List<Long> organizationType;
    List<Long> organizationSubType;
    List<Long> region;
    List<Long> level;

    public OrganizationMappingActivity(List<Long> expertize, List<Long> organizationType, List<Long> organizationSubType, List<Long> region, List<Long> level) {
        this.expertize = expertize;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.region = region;
        this.level = level;
    }

    public List<Long> getExpertize() {
        return expertize;
    }

    public void setExpertize(List<Long> expertize) {
        this.expertize = expertize;
    }

    public List<Long> getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(List<Long> organizationType) {
        this.organizationType = organizationType;
    }

    public List<Long> getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(List<Long> organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public List<Long> getRegion() {
        return region;
    }

    public void setRegion(List<Long> region) {
        this.region = region;
    }

    public List<Long> getLevel() {
        return level;
    }

    public void setLevel(List<Long> level) {
        this.level = level;
    }
}
