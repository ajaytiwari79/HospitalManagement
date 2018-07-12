package com.kairos.persistence.model.agreement.cta.cta_response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.agreement.cta.CTARuleTemplateDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiveTimeAgreementDTO {
    private Long id;
    private String name;
    private String description;
    @NotNull(message = "error.cta.expertise.notNull")
    private Long expertise;
    private Long organizationType;
    private Long organizationSubType;
    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    @NotNull(message = "error.cta.startDate.notNull")
    private Long startDateMillis;
    private Long endDateMillis;
    private boolean disabled;

    public CollectiveTimeAgreementDTO() {

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

    public List<CTARuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public CollectiveTimeAgreementDTO(String name, String description, Long expertiseId, Long organizationTypeId, Long organizationSubTypeId, Long startDateMillis, List<CTARuleTemplateDTO> ruleTemplates) {
        this.setName(name);
        this.setDescription(description);
        this.setExpertise(expertiseId);
        this.setOrganizationType(organizationTypeId);
        this.setOrganizationSubType(organizationSubTypeId);
        this.setRuleTemplates(ruleTemplates);
        this.setStartDateMillis(startDateMillis);
    }

    @Override
    public String toString() {
        return "CollectiveTimeAgreementDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", expertise=" + expertise +
                ", organizationType=" + organizationType +
                ", organizationSubType=" + organizationSubType +
                ", ruleTemplates=" + ruleTemplates +
                ", disabled=" + disabled +
                '}';
    }
}
