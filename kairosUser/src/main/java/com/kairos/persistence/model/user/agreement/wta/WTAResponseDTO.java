package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by vipul on 21/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
@Deprecated
public class WTAResponseDTO {

    private List<RuleTemplateCategoryDTO> ruleTemplates;

    private WTAResponseDTO parentWTA;

    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private String name;
    private String description;
    private long id;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;

    private List<Tag> tags;


    public WTAResponseDTO() {
        //default
    }

    public WTAResponseDTO(Long id, Long startDateMillis, Long endDateMillis, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.name = name;
        this.description = description;


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

    public List<RuleTemplateCategoryDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplateCategoryDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public WTAResponseDTO getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(WTAResponseDTO parentWTA) {
        this.parentWTA = parentWTA;
    }

    public WTAResponseDTO(Long id, Long startDateMillis, Long endDateMillis, String name, String description, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, List<Tag> tags) {
        this.startDateMillis = startDateMillis;
        this.id = id;
        this.endDateMillis = endDateMillis;
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.tags = tags;
    }
}