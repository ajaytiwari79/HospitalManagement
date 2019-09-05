package com.kairos.persistence.model.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.wta.WTATemplateType.WTA_FOR_BREAKS_IN_SHIFT;

/**
 * @author pradeep
 * @date - 13/4/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTAQueryResultDTO {

    private BigInteger parentId;

    private BigInteger countryParentWTA;

    private BigInteger organizationParentId; // wta id of parent organization and this must not be changable
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expiryDate;
    private String name;
    private String description;
    private Long employmentId;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationDTO organization;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private List<WTAQueryResultDTO> versions = new ArrayList<>();
    private List<TagDTO> tags = new ArrayList<>();


    private List<WTABaseRuleTemplate> ruleTemplates;

    public List<WTAQueryResultDTO> getVersions() {
        return versions;
    }

    public void setVersions(List<WTAQueryResultDTO> versions) {
        this.versions = versions;
    }

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }


    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return Optional.ofNullable(ruleTemplates).orElse(new ArrayList<>());
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public BigInteger getCountryParentWTA() {
        return countryParentWTA;
    }

    public void setCountryParentWTA(BigInteger countryParentWTA) {
        this.countryParentWTA = countryParentWTA;
    }

    public BigInteger getOrganizationParentId() {
        return organizationParentId;
    }

    public void setOrganizationParentId(BigInteger organizationParentId) {
        this.organizationParentId = organizationParentId;
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

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public boolean isValidWorkTimeAgreement(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }

    public BreakWTATemplate getBreakRule(){
        BreakWTATemplate breakWTATemplate=(BreakWTATemplate)
        this.getRuleTemplates().stream().filter(current->current.getWtaTemplateType().toString().equals(WTA_FOR_BREAKS_IN_SHIFT.toString())).findFirst().orElse(null);
        return breakWTATemplate;
    }

}
