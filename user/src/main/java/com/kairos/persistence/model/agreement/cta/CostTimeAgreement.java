package com.kairos.persistence.model.agreement.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class CostTimeAgreement extends UserBaseEntity {
    private String name;
    private String description;
    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;
    @Relationship(type = BELONGS_TO_ORG_TYPE)
    private OrganizationType organizationType;
    @Relationship(type = BELONGS_TO_ORG_SUB_TYPE)
    private OrganizationType organizationSubType;
    @Relationship(type = BELONGS_TO)
    private Country country;
    @Relationship(type = HAS_PARENT_CTA)
    private CostTimeAgreement parent;
    @Relationship(type = HAS_PARENT_COUNTRY_CTA)
    private CostTimeAgreement parentCountryCTA;
    @Relationship(type = HAS_RULE_TEMPLATE)
    private List<RuleTemplate> ruleTemplates=new ArrayList<>();
    private Long startDateMillis;
    private Long endDateMillis;
    private boolean disabled;
    @Relationship(type = BELONGS_TO)
    private User createdBy;
    @Relationship(type = BELONGS_TO)
    private User lastModifiedBy;

    public CostTimeAgreement() {
    }

    public CostTimeAgreement(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public List<RuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CostTimeAgreement getParent() {
        return parent;
    }

    public void setParent(CostTimeAgreement parent) {
        this.parent = parent;
    }
    public boolean hasParent(){
        return true;
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

    public CostTimeAgreement getParentCountryCTA() {
        return parentCountryCTA;
    }

    public void setParentCountryCTA(CostTimeAgreement parentCountryCTA) {
        this.parentCountryCTA = parentCountryCTA;
    }

    public void addRuleTemplate(RuleTemplate ruleTemplate) {
        if (ruleTemplate == null)
            throw new NullPointerException("Can't add null ruleTemplateCategory");
        getRuleTemplates().add(ruleTemplate);

    }

    public void removeOrganizationType(RuleTemplate ruleTemplate) {
        if (ruleTemplate == null)
            getRuleTemplates().remove(ruleTemplate);
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        if(this.getId()!=null)
            throw new UnsupportedOperationException("can't modified this property");
        this.createdBy = createdBy;
        this.createdBy = createdBy;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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
                .append(country, that.country)
                .append(parent, that.parent)
                .append(ruleTemplates, that.ruleTemplates)
                .append(startDateMillis, that.startDateMillis)
                .append(endDateMillis, that.endDateMillis)
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
                .append(country)
                .append(parent)
                .append(ruleTemplates)
                .append(startDateMillis)
                .append(endDateMillis)
                .append(disabled)
                .toHashCode();
    }
}
