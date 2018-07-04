package com.kairos.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.user.country.tag.TagDTO;
import com.kairos.user.organization.OrganizationTypeDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 21/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class WTAResponseDTO {

    private List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> ruleTemplates;

    private BigInteger parentWTA;

    private Long startDateMillis;
    private Long endDateMillis;
    private Date startDate;
    private Date endDate;
    private Long expiryDate;
    private String name;
    private String description;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private WTAResponseDTO parentWTAResponse;
    private List<TagDTO> tags;

    public WTAResponseDTO getParentWTAResponse() {
        return parentWTAResponse;
    }

    public void setParentWTAResponse(WTAResponseDTO parentWTAResponse) {
        this.parentWTAResponse = parentWTAResponse;
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

    public WTAResponseDTO() {
        //default
    }

    public WTAResponseDTO(BigInteger id, Long startDateMillis, Long endDateMillis, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description) {
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<com.kairos.activity.wta.country.WTABaseRuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public BigInteger getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(BigInteger parentWTA) {
        this.parentWTA = parentWTA;
    }

    public WTAResponseDTO(BigInteger id, Long startDateMillis, Long endDateMillis, String name, String description, ExpertiseResponseDTO expertise, OrganizationTypeDTO organizationType, OrganizationTypeDTO organizationSubType, List<TagDTO> tags) {
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