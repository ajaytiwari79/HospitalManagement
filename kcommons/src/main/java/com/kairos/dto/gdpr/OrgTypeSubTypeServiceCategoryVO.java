package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class OrgTypeSubTypeServiceCategoryVO {

    private Long id;
    private String name;
    private Long countryId;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() { return organizationSubTypes; }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public List<ServiceCategoryDTO> getOrganizationServices() { return organizationServices; }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() { return organizationSubServices; }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }
}
