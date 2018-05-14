package com.kairos.response.dto.web.organizationtype_service_dto;

import java.util.List;


public class OrganizationTypeAndSubTypeResponseDto {

    private Long id;
    private String name;

    private List<OrganizationTypeAndSubTypeResponseDto> or_sub_Types;
    private List<OrganizationServiceResponseDto> services;

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

    public List<OrganizationTypeAndSubTypeResponseDto> getOr_sub_Types() {
        return or_sub_Types;
    }

    public void setOr_sub_Types(List<OrganizationTypeAndSubTypeResponseDto> or_sub_Types) {
        this.or_sub_Types = or_sub_Types;
    }

    public List<OrganizationServiceResponseDto> getServices() {
        return services;
    }

    public void setServices(List<OrganizationServiceResponseDto> services) {
        this.services = services;
    }

    public OrganizationTypeAndSubTypeResponseDto()
    {}
}
