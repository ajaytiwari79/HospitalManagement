package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class MasterProcessingActivityDeprecated {

    @NotBlank(message = "Name can't be empty")
    private String name;
    private String description;
    private List<OrganizationTypeDTO> organizationTypeDTOS;
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private List<BigInteger> subProcessingActivityIds;
    private Long countryId;
    private List<BigInteger> risks=new ArrayList<>();
    private boolean subProcess;
    private boolean hasSubProcessingActivity;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterProcessingActivityDeprecated() {

    }


    public MasterProcessingActivityDeprecated(String name, String description, SuggestedDataStatus suggestedDataStatus, List<OrganizationTypeDTO> organizationTypeDTOS, List<OrganizationSubTypeDTO> organizationSubTypeDTOS, List<ServiceCategoryDTO> organizationServices, List<SubServiceCategoryDTO> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.organizationTypeDTOS = organizationTypeDTOS;
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
        this.suggestedDataStatus=suggestedDataStatus;
    }

    public MasterProcessingActivityDeprecated(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.subProcess = subProcess;
        this.suggestedDataStatus=suggestedDataStatus;
        this.suggestedDate=suggestedDate;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isSubProcess() { return subProcess; }

    public MasterProcessingActivityDeprecated setSubProcess(boolean subProcess) { this.subProcess = subProcess; return this;}

    public boolean isHasSubProcessingActivity() { return hasSubProcessingActivity; }

    public MasterProcessingActivityDeprecated setHasSubProcessingActivity(boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity;return this; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getSubProcessingActivityIds() {
        return subProcessingActivityIds;
    }

    public void setSubProcessingActivityIds(List<BigInteger> subProcessingActivityIds) { this.subProcessingActivityIds = subProcessingActivityIds; }

    public String getName() {
        return name;
    }

    public MasterProcessingActivityDeprecated setName(String name) { this.name = name; return this; }

    public String getDescription() {
        return description;
    }

    public MasterProcessingActivityDeprecated setDescription(String description) { this.description = description; return this; }

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public MasterProcessingActivityDeprecated setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) { this.organizationTypeDTOS = organizationTypeDTOS;  return this;}

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public MasterProcessingActivityDeprecated setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) { this.organizationSubTypeDTOS = organizationSubTypeDTOS; return this; }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public MasterProcessingActivityDeprecated setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices;  return this;}

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterProcessingActivityDeprecated setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public List<BigInteger> getRisks() { return risks;}

    public void setRisks(List<BigInteger> risks) { this.risks = risks; }


}
