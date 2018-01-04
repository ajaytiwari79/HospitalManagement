package com.kairos.persistence.model.user.agreement.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 26/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class CTAListQueryResult {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private Long expertise;
    private Long organizationType;
    private Long organizationSubType;
    private List<CTARuleTemplateQueryResult> ruleTemplates = new ArrayList<>();
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
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
}
