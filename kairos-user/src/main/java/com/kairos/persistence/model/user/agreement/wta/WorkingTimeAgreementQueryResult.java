package com.kairos.persistence.model.user.agreement.wta;
import com.kairos.persistence.model.user.expertise.ExpertiseDTO;

import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;
import com.kairos.persistence.model.organization.OrganizationTypeDTO;
import java.util.List;

/**
 * Created by vipul on 9/8/17.
 */
@QueryResult
public class WorkingTimeAgreementQueryResult {
    private Long startDate;
    private long creationDate;
    private Long endDate;
    private Long expiryDate;
    private String name;
    private String description;
    private long id;
    private ExpertiseDTO expertise;
    private OrganizationTypeDTO organizationTypes;//
    private OrganizationTypeDTO organizationSubTypes;//

    private List<WTABaseRuleTemplate> ruleTemplates;

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
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

    public ExpertiseDTO getExpertise() {
        return expertise;
    }

    public void setExpertise(ExpertiseDTO expertise) {
        this.expertise = expertise;
    }


    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public OrganizationTypeDTO getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(OrganizationTypeDTO organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public OrganizationTypeDTO getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(OrganizationTypeDTO organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }
}
