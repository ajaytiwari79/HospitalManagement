package com.kairos.wrapper;

import java.util.List;

public class OrganizationServiceDto {


    private Long id;
    private String name;
    private List<OrganizationServiceDto>  organizationSubServices;

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

    public List<OrganizationServiceDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationServiceDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
