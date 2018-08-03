package com.kairos.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 26/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CTAListQueryResult {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private Long expertise;
    private Long organizationType;
    private Long organizationSubType;
    private List<CTARuleTemplateQueryResult> ruleTemplates = new ArrayList<>();
    private Long startDateMillis;
    private Long endDateMillis;
    public CTAListQueryResult(){
        // default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getExpertise() {
        return expertise;
    }

    public void setExpertise(Long expertise) {
        this.expertise = expertise;
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

    public List<CTARuleTemplateQueryResult> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<CTARuleTemplateQueryResult> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }
}
