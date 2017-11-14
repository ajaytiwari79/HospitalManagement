package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by pawanmandhan on 2/8/17.
 */
public class WtaDTO {


    private String name;
    private String description;
    private long expertiseId;
    private Long organizationType;
    private Long organizationSubType;
    private List<Long> ruleTemplates;
    private long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
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


    public List<Long> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<Long> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Long organizationType) {
        this.organizationType = organizationType;
    }

    public Long getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(Long organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public WtaDTO() {
    }

    public WtaDTO(String name, String description, long expertiseId, Long organizationType, Long organizationSubType, long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.name = name;
        this.description = description;
        this.expertiseId = expertiseId;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }
}
