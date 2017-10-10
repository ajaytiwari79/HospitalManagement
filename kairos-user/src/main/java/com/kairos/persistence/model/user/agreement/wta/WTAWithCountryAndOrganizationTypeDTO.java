package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 21/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class WTAWithCountryAndOrganizationTypeDTO {

    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private String name;
    private String description;
    private long id;
    private boolean isEnabled ;

    private Expertise expertise;

    private OrganizationType organizationTypes;//
    private OrganizationType organizationSubTypes;//
    private List<WTABaseRuleTemplate> ruleTemplates;

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

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public OrganizationType getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(OrganizationType organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public OrganizationType getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(OrganizationType organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public WorkingTimeAgreement build(){
        WorkingTimeAgreement wta=new WorkingTimeAgreement(this.name, this.description, this.expertise,this.organizationTypes, this.organizationSubTypes, this.ruleTemplates, this.startDateMillis, this.endDateMillis,this.expiryDate);
        return wta;
    }
}