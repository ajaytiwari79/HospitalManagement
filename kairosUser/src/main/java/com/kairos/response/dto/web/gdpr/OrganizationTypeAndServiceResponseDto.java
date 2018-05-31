package com.kairos.response.dto.web.gdpr;

import com.kairos.persistence.model.organization.OrganizationBasicResponse;

import java.util.List;

public class OrganizationTypeAndServiceResponseDto {


    private List<OrganizationBasicResponse> organizationTypes;
    private List<OrganizationBasicResponse> organizationSubTypes;
    private List<OrganizationBasicResponse> organizationServices;
    private List<OrganizationBasicResponse> organizationSubServices;

    public List<OrganizationBasicResponse> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationBasicResponse> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationBasicResponse> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationBasicResponse> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationBasicResponse> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationBasicResponse> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationBasicResponse> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationBasicResponse> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
    public OrganizationTypeAndServiceResponseDto()
    {}

}
