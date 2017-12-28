package com.kairos.response.dto.web.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateDTO;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiveTimeAgreementDTO {
    private Long id;
    private String name;
    private String description;
    private Long expertise;
    private Long organizationType;
    private Long organizationSubType;
    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    private Date startDate;
    private Date endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

/*public List<Long> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<Long> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }*/

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

    public CollectiveTimeAgreementDTO(String name, String description, Long expertiseId, Long organizationTypeId, Long organizationSubTypeId, List<CTARuleTemplateDTO> ruleTemplates) {
        this.setName(name);
        this.setDescription(description);
        this.setExpertise(expertiseId);
        this.setOrganizationType(organizationTypeId);
        this.setOrganizationSubType(organizationSubTypeId);
        this.setRuleTemplates(ruleTemplates);
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
