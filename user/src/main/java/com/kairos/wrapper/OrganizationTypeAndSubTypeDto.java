package com.kairos.wrapper;

import java.util.List;

public class OrganizationTypeAndSubTypeDto {


    private Long id;
    private String name;

    private List<OrganizationTypeAndSubTypeDto> organizationSubTypes;
    private List<OrganizationServiceDto> organizationServices;

    public OrganizationTypeAndSubTypeDto() {
        //Default Constructor
    }

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

    public List<OrganizationTypeAndSubTypeDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndSubTypeDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationServiceDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationServiceDto> organizationServices) {
        this.organizationServices = organizationServices;
    }
}
