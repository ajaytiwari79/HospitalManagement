package com.kairos.response.dto.web.organizationtype_service_dto;

import java.util.List;


public class OrganizationTypeAndSubTypeResponseDto {

    private Long id;
    private String name;

    private List<OrganizationTypeAndSubTypeResponseDto> organizationSubTypes;
    private List<OrganizationServiceResponseDto> organizationServices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationTypeAndSubTypeResponseDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndSubTypeResponseDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationServiceResponseDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationServiceResponseDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public OrganizationTypeAndSubTypeResponseDto()
    {}
}
