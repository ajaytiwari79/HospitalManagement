package com.kairos.activity.cta;


import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.user.organization.OrganizationTypeDTO;
import com.kairos.user.organization.position_code.PositionCodeDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pavan on 16/4/18.
 */

public class CTAResponseDTO {
    @NotNull
    private BigInteger id;
    private BigInteger parentCTAId;
    private String name;
    private String description;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    // Added for version of CTA
    private List<CTAResponseDTO> versions = new ArrayList<>();
    private Map<String, Object> unitInfo;
    private PositionCodeDTO positionCode;
    private Long unitPositionId;
    private Boolean disabled;

    public CTAResponseDTO() {
        //Default constructor
    }

    public CTAResponseDTO(@NotNull BigInteger id, String name, ExpertiseResponseDTO expertise, List<CTARuleTemplateDTO> ruleTemplates, LocalDate startDate, LocalDate endDate, Boolean disabled,Long unitPositionId) {
        this.id = id;
        this.name = name;
        this.expertise = expertise;
        this.ruleTemplates = ruleTemplates;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
        this.unitPositionId = unitPositionId;
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

    public List<CTARuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public PositionCodeDTO getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCodeDTO positionCode) {
        this.positionCode = positionCode;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getParentCTAId() {
        return parentCTAId;
    }

    public void setParentCTAId(BigInteger parentCTAId) {
        this.parentCTAId = parentCTAId;
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

    public List<CTAResponseDTO> getVersions() {
        return versions;
    }

    public void setVersions(List<CTAResponseDTO> versions) {
        this.versions = versions;
    }

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }


    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
