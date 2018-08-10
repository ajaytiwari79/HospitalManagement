package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.OrganizationSubType;
import com.kairos.gdpr.OrganizationType;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSubjectMappingResponseDTO {

    private BigInteger id;

    private String name;

    private String description;

    private Long countryId;

    private List<OrganizationType> organizationTypes;

    private List<OrganizationSubType> organizationSubTypes;

    private Set<DataCategoryResponseDTO> dataCategories;

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

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<DataCategoryResponseDTO> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Set<DataCategoryResponseDTO> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


}
