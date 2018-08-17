package com.kairos.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.user.organization.OrganizationTypeDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiveTimeAgreementDTO {
    private BigInteger id;
    @NotNull(message = "error.cta.expertise.notNull")
    private String name;
    private String description;
    @NotNull(message = "error.cta.parentExpertise.notNull")
    private ExpertiseResponseDTO expertise;
    @NotNull(message = "error.cta.organizationType.notNull")
    private OrganizationTypeDTO organizationType;
    @NotNull(message = "error.cta.organizationSubType.notNull")
    private OrganizationTypeDTO organizationSubType;
    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    @NotNull(message = "error.cta.startDate.notNull")
    private LocalDate startDate;
    private LocalDate endDate;

    public CollectiveTimeAgreementDTO() {

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

    public ExpertiseResponseDTO getExpertise() {
        return expertise;
    }

    public void setExpertise(ExpertiseResponseDTO expertise) {
        this.expertise = expertise;
    }

    public OrganizationTypeDTO getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeDTO organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationTypeDTO getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationTypeDTO organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<CTARuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
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
                '}';
    }
}

