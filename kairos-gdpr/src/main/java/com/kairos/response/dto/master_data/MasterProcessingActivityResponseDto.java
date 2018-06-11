package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityResponseDto {

    @NotNull
    private BigInteger id;

    @NotNullOrEmpty
    private String name;

    @NotNullOrEmpty
    private String description;

    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;
    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;

    private List<MasterProcessingActivityResponseDto> subProcessingActivities;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    private Long countryId;

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

    public List<MasterProcessingActivityResponseDto> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivities(List<MasterProcessingActivityResponseDto> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public MasterProcessingActivityResponseDto()
    {}

}
