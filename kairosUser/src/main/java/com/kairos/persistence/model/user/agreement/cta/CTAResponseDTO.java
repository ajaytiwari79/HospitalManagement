package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 16/4/18.
 */
@QueryResult
public class CTAResponseDTO {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;
    private List<CTARuleTemplateQueryResult> ruleTemplates = new ArrayList<>();
    private Long startDateMillis;
    private Long endDateMillis;

    public CTAResponseDTO() {
        //Default constructor
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

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationType organizationSubType) {
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
