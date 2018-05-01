package com.kairos.response.dto.web.wta;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kairos.activity.persistence.enums.WTATemplateType;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by Pradeep on 27/4/18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "wtaTemplateType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AverageScheduledTimeWTATemplateDTO.class, name = "AVERAGE_SHEDULED_TIME")})
public class WTABaseRuleTemplateDTO{

    protected BigInteger id;
    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger ruleTemplateCategoryId;
    protected String lastUpdatedBy;
    protected Long countryId;
    protected RuleTemplateCategoryDTO ruleTemplateCategory;
    private WTATemplateType wtaTemplateType;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    protected List<PhaseTemplateValueDTO> phaseTemplateValues;

    public RuleTemplateCategoryDTO getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategoryDTO ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public BigInteger getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategoryId(BigInteger ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public WTABaseRuleTemplateDTO(){}

    public WTABaseRuleTemplateDTO(String name, String description) {
        this.name = name;
        this.description = description;
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


    public List<PhaseTemplateValueDTO> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValueDTO> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }


}
