package com.kairos.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
/**
 * Created by vipul on 21/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTADTO {
    private BigInteger id;
    private String name;
    private String description;
    private long expertiseId;
    private long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    @NotNull(message = "error.RuleTemplate.description.notnull")
    private List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> ruleTemplates;
    private Long organizationType;
    private Long organizationSubType;
    private List<BigInteger> tags;

    public WTADTO() {
        //default cons
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> getRuleTemplates() {
        return this.ruleTemplates;
    }

    public void setRuleTemplates(List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> ruleTemplates) {
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

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public WTADTO(String name, String description, long expertiseId, long startDateMillis, Long endDateMillis, Long expiryDate, List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> ruleTemplates, Long organizationType, Long organizationSubType) {
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
