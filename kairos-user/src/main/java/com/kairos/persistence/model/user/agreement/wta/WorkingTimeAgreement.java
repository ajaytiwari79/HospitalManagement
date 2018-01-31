package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity

/**
 * @Author vipul
 *
 * @Modified added organization and staff for personal copy
 */
public class WorkingTimeAgreement extends UserBaseEntity {

    @NotNull(message = "error.WorkingTimeAgreement.name.notnull")
    private String name;

    private String description;
    // This will be only used when the country will update the WTA a new Copy of WTA will be assigned to organization having state disabled
    private Boolean disabled;

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = BELONGS_TO_ORG_TYPE)
    private OrganizationType organizationType;

    @Relationship(type = BELONGS_TO_ORG_SUB_TYPE)
    private OrganizationType organizationSubType;

    @JsonIgnore
    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = HAS_WTA)
    private Organization organization;


    @Relationship(type = HAS_RULE_TEMPLATE)
    private List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<WTABaseRuleTemplate>();

    // to make a history
    @Relationship(type = HAS_PARENT_WTA)
    private WorkingTimeAgreement parentWTA;

    @Relationship(type = HAS_COUNTRY_PARENT_WTA)
    private WorkingTimeAgreement countryParentWTA;

    @Relationship(type = HAS_ORGANIZATION_PARENT_WTA)
    private WorkingTimeAgreement organizationParentWTA;


    @Relationship(type = HAS_TAG)
    private List<Tag> tags = new ArrayList<>();

    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
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

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public WorkingTimeAgreement getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(WorkingTimeAgreement parentWTA) {
        this.parentWTA = parentWTA;
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

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public WorkingTimeAgreement getCountryParentWTA() {
        return countryParentWTA;
    }

    public void setCountryParentWTA(WorkingTimeAgreement countryParentWTA) {
        this.countryParentWTA = countryParentWTA;
    }

    public WorkingTimeAgreement getOrganizationParentWTA() {
        return organizationParentWTA;
    }

    public void setOrganizationParentWTA(WorkingTimeAgreement organizationParentWTA) {
        this.organizationParentWTA = organizationParentWTA;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("code", this.description);
        map.put("lastModificationDate", this.getLastModificationDate());
        map.put("creationDate", this.getCreationDate());
        return map;
    }

    public WorkingTimeAgreement() {
        //default
    }

    public WorkingTimeAgreement(@NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description, Boolean disabled, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, Country country, Organization organization, List<WTABaseRuleTemplate> ruleTemplates, WorkingTimeAgreement parentWTA, WorkingTimeAgreement countryParentWTA, WorkingTimeAgreement organizationParentWTA, List<Tag> tags, Long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.name = name;
        this.description = description;
        this.disabled = disabled;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.country = country;
        this.organization = organization;
        this.ruleTemplates = ruleTemplates;
        this.parentWTA = parentWTA;
        this.countryParentWTA = countryParentWTA;
        this.organizationParentWTA = organizationParentWTA;
        this.tags = tags;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }

    public WorkingTimeAgreement(String name, String description, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, Country country, List<WTABaseRuleTemplate> ruleTemplates, WorkingTimeAgreement parentWTA, Long startDateMillis, Long endDateMillis, Long expiryDate, boolean deleted) {
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.country = country;
        this.ruleTemplates = ruleTemplates;
        this.parentWTA = parentWTA;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
        this.deleted = deleted;
    }

    public WorkingTimeAgreement(String name, String description, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, List<WTABaseRuleTemplate> ruleTemplates, Long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.ruleTemplates = ruleTemplates;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }


    public WorkingTimeAgreement(Long id, String name, String description, Long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }

    public WorkingTimeAgreement basicDetails() {
        WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement(this.id, this.name, this.description, this.startDateMillis, this.endDateMillis, this.expiryDate);
        return workingTimeAgreement;
    }

    public WTAResponseDTO retriveBasicResponse() {
        WTAResponseDTO responseDTO = new WTAResponseDTO(this.startDateMillis, this.endDateMillis, this.name, this.description, this.expertise.retrieveBasicDetails(), this.organizationType.basicDetails(), this.organizationSubType.basicDetails(), this.tags);
        return responseDTO;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("description", description)
                .append("expiryDate", expiryDate)
                .toString();
    }
}
