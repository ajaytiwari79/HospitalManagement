package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private  String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;


    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private Set<OrganizationTypeDTO> organizationTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private Set<OrganizationSubTypeDTO> organizationSubTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private Set<ServiceCategoryDTO> organizationServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private Set<SubServiceCategoryDTO> organizationSubServices=new HashSet<>();

    private List<MasterProcessingActivityDTO> subProcessingActivities=new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MasterProcessingActivityDTO> getSubProcessingActivities() {
        return subProcessingActivities;
    }

   public void setSubProcessingActivities(List<MasterProcessingActivityDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

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

    public Set<OrganizationTypeDTO> getOrganizationTypes() { return organizationTypes; }

    public void setOrganizationTypes(Set<OrganizationTypeDTO> organizationTypes) { this.organizationTypes = organizationTypes; }

    public Set<OrganizationSubTypeDTO> getOrganizationSubTypes() { return organizationSubTypes; }

    public void setOrganizationSubTypes(Set<OrganizationSubTypeDTO> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public Set<ServiceCategoryDTO> getOrganizationServices() { return organizationServices; }

    public void setOrganizationServices(Set<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public Set<SubServiceCategoryDTO> getOrganizationSubServices() { return organizationSubServices; }

    public void setOrganizationSubServices(Set<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivityDTO()
    {

    }
}





