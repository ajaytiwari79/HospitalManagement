package com.kairos.persistence.model.organization_type;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class OrganizationTypeSubTypeAndServicesQueryResult {

    private Long id;
    private String name;
    private Long countryId;
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() { return organizationSubTypeDTOS; }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) { this.organizationSubTypeDTOS = organizationSubTypeDTOS; }

    public List<ServiceCategoryDTO> getOrganizationServices() { return organizationServices; }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() { return organizationSubServices; }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }
}
