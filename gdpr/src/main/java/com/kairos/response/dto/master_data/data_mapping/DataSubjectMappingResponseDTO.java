package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSubjectMappingResponseDTO {

    private BigInteger id;

    private String name;

    private String description;

    private Long countryId;

    private List<OrganizationTypeDTO> organizationTypeDTOS;

    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;

    private List<DataCategoryResponseDTO> dataCategories;

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

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public void setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) {
        this.organizationTypeDTOS = organizationTypeDTOS;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) {
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
    }

    public List<DataCategoryResponseDTO> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(List<DataCategoryResponseDTO> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


}
