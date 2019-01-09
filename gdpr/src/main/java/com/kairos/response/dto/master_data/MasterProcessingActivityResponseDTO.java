package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityResponseDTO {

    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private List<OrganizationTypeDTO> organizationTypes;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private List<MasterProcessingActivityResponseDTO> subProcessingActivities=new ArrayList<>();
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;
    private Boolean hasSubProcessingActivity;

    public MasterProcessingActivityResponseDTO(@NotNull Long id, @NotBlank String name, @NotBlank String description, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public Boolean getHasSubProcessingActivity() { return hasSubProcessingActivity; }

    public void setHasSubProcessingActivity(Boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }


    public List<MasterProcessingActivityResponseDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<MasterProcessingActivityResponseDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() {
        return suggestedDataStatus;
    }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus;
    }

    public MasterProcessingActivityResponseDTO() {
    }

}
