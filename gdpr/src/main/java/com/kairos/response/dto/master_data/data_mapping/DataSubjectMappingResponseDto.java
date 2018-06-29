package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSubjectMappingResponseDto {

    private BigInteger id;

    private String name;

    private String description;

    private Long countryId;

    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;

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

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
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
