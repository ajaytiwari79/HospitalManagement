package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSubjectMappingResponseDto {

    private BigInteger id;

    private String name;

    private String description;

    private Long countryId;

    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    private List<OrganizationTypeAndServiceBasicDto> organizationServices;

    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;

    private Set<DataCategoryResponseDto> dataCategories;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public Set<DataCategoryResponseDto> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Set<DataCategoryResponseDto> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


}
