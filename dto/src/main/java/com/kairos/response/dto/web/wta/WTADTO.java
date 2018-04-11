package com.kairos.response.dto.web.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by vipul on 21/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTADTO {
    private String name;
    private String description;
    private long expertiseId;
    private long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private List<RuleTemplateCategoryDTO> ruleTemplates;
    private Long organizationType;
    private Long organizationSubType;
    private List<Long> tags;

    public WTADTO() {
        //default cons
    }

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

    public List<RuleTemplateCategoryDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplateCategoryDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
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

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public WTADTO(String name, String description, long expertiseId, long startDateMillis, Long endDateMillis, Long expiryDate, List<RuleTemplateCategoryDTO> ruleTemplates, Long organizationType, Long organizationSubType) {
        this.name = name;
        this.description = description;
        this.expertiseId = expertiseId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
        this.ruleTemplates = ruleTemplates;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WTADTO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", expertiseId=").append(expertiseId);
        sb.append(", startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", expiryDate=").append(expiryDate);
        sb.append(", ruleTemplates=").append(ruleTemplates);
        sb.append(", organizationType=").append(organizationType);
        sb.append(", organizationSubType=").append(organizationSubType);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
