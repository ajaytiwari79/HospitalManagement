package com.kairos.activity.persistence.model.wta;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * @Author pradeep singh
 *
 * @Modified added organization and staff for personal copy
 */
@Document
public class WorkingTimeAgreement extends MongoBaseEntity {

    @NotNull(message = "error.WorkingTimeAgreement.name.notnull")
    private String name;

    private String description;
    // This will be only used when the countryId will update the WTA a new Copy of WTA will be assigned to organization having state disabled
    private Boolean disabled;

    private WTAExpertise expertise;

    private WTAOrganizationType organizationType;

    private WTAOrganizationType organizationSubType;


    private Long countryId;

    private WTAOrganization organization;

    private List<BigInteger> ruleTemplateIds;

    private List<WTABaseRuleTemplate> ruleTemplates;


    // to make a history
    private BigInteger parentWTA;

    private BigInteger countryParentWTA;

    private BigInteger organizationParentWTA;


    private List<BigInteger> tags = new ArrayList<>();

    private Date startDate;
    private Date endDate;
    private Date expiryDate;

    public List<BigInteger> getRuleTemplateIds() {
        return ruleTemplateIds;
    }

    public void setRuleTemplateIds(List<BigInteger> ruleTemplateIds) {
        this.ruleTemplateIds = ruleTemplateIds;
    }


    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public WTAOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(WTAOrganization organization) {
        this.organization = organization;
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

    public WTAExpertise getExpertise() {
        return expertise;
    }

    public void setExpertise(WTAExpertise expertise) {
        this.expertise = expertise;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public BigInteger getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(BigInteger parentWTA) {
        this.parentWTA = parentWTA;
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public WTAOrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(WTAOrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public WTAOrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(WTAOrganizationType organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public BigInteger getCountryParentWTA() {
        return countryParentWTA;
    }

    public void setCountryParentWTA(BigInteger countryParentWTA) {
        this.countryParentWTA = countryParentWTA;
    }

    public BigInteger getOrganizationParentWTA() {
        return organizationParentWTA;
    }

    public void setOrganizationParentWTA(BigInteger organizationParentWTA) {
        this.organizationParentWTA = organizationParentWTA;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }


    public WorkingTimeAgreement(BigInteger id, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description, Date startDate, Date endDate, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
    }

    public WorkingTimeAgreement() {
        //default
    }
    public WorkingTimeAgreement basicDetails() {
        return new WorkingTimeAgreement(this.id, this.name, this.description, this.startDate, this.endDate, this.expiryDate);
    }


}
