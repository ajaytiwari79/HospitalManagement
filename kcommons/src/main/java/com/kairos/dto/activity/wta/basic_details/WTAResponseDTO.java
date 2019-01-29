package com.kairos.dto.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 21/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WTAResponseDTO {

    private List<WTABaseRuleTemplateDTO> ruleTemplates;

    private BigInteger parentId;
    private BigInteger organizationParentId;// wta id of parent organization and this must not be changable
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expiryDate;
    private String name;
    private Long unitPositionId;
    private String description;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private WTAResponseDTO parentWTAResponse;
    private List<WTAResponseDTO> versions = new ArrayList<>();
    private List<TagDTO> tags;
    private Map<String, Object> unitInfo;

    public WTAResponseDTO() {
        //default
    }

    public WTAResponseDTO(String name, BigInteger id,BigInteger parentId) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }

    public WTAResponseDTO(BigInteger id, LocalDate startDate, LocalDate endDate, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description) {
        this.id = id;
        this.startDate =startDate;
        this.endDate = endDate;
        this.name = name;
        this.description = description;


    }
    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }

    public List<WTAResponseDTO> getVersions() {
        return versions;
    }

    public void setVersions(List<WTAResponseDTO> versions) {
        this.versions = versions;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public WTAResponseDTO getParentWTAResponse() {
        return parentWTAResponse;
    }

    public void setParentWTAResponse(WTAResponseDTO parentWTAResponse) {
        this.parentWTAResponse = parentWTAResponse;
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

    public List<WTABaseRuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public BigInteger getOrganizationParentId() {
        return organizationParentId;
    }

    public void setOrganizationParentId(BigInteger organizationParentId) {
        this.organizationParentId = organizationParentId;
    }

    public WTAResponseDTO(BigInteger id, LocalDate startDate, LocalDate endDate, String name, String description, ExpertiseResponseDTO expertise, OrganizationTypeDTO organizationType, OrganizationTypeDTO organizationSubType, List<TagDTO> tags) {
        this.startDate = startDate;
        this.id = id;
        this.endDate = endDate;
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.tags = tags;
    }

}