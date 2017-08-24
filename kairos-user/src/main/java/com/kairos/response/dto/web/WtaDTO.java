package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by pawanmandhan on 2/8/17.
 */
public class WtaDTO {


    private String      name;
    private String      description;
    private long        expertiseId;
    private List<Long> organizationTypes;
    private List<Long> ruleTemplates;
    private long        regionId;
    private long        startDate;
    private Long        endDate;
    private Long        expiryDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<Long> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
