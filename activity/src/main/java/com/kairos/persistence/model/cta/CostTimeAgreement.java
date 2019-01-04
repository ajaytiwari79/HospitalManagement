package com.kairos.persistence.model.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.OrganizationType;
import com.kairos.persistence.model.wta.WTAOrganization;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
public class CostTimeAgreement extends MongoBaseEntity {
    private String name;
    private String description;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;
    private Long countryId;
    private BigInteger organizationParentId;
    private WTAOrganization organization;
    private BigInteger parentId;
    private BigInteger parentCountryCTAId;
    private List<BigInteger> ruleTemplateIds =new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean disabled;
    private Long createdBy;
    private Long lastModifiedBy;
    private Long unitPositionId;

    public CostTimeAgreement() {
    }


    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
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

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public List<BigInteger> getRuleTemplateIds() {
        return ruleTemplateIds;
    }

    public void setRuleTemplateIds(List<BigInteger> ruleTemplateIds) {
        this.ruleTemplateIds = ruleTemplateIds;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }
    public boolean hasParent(){
        return true;
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

    public BigInteger getParentCountryCTAId() {
        return parentCountryCTAId;
    }

    public void setParentCountryCTAId(BigInteger parentCountryCTAId) {
        this.parentCountryCTAId = parentCountryCTAId;
    }


    public void removeOrganizationType(CTARuleTemplate ruleTemplate) {
        if (ruleTemplate == null)
            getRuleTemplateIds().remove(ruleTemplate);
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

    public Long getCreatedBy() {
        return createdBy;
    }


    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public BigInteger getOrganizationParentId() {
        return organizationParentId;
    }

    public void setOrganizationParentId(BigInteger organizationParentId) {
        this.organizationParentId = organizationParentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostTimeAgreement that = (CostTimeAgreement) o;

        return new EqualsBuilder()
                .append(disabled, that.disabled)
                .append(name, that.name)
                .append(description, that.description)
                .append(expertise, that.expertise)
                .append(organizationType, that.organizationType)
                .append(organizationSubType, that.organizationSubType)
                .append(countryId, that.countryId)
                .append(parentId, that.parentId)
                .append(ruleTemplateIds, that.ruleTemplateIds)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(description)
                .append(expertise)
                .append(organizationType)
                .append(organizationSubType)
                .append(countryId)
                .append(parentId)
                .append(ruleTemplateIds)
                .append(startDate)
                .append(endDate)
                .append(disabled)
                .toHashCode();
    }
}

