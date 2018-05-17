package com.kairos.client.dto;

import java.util.Set;

public class OrganizationTypeAndServiceRestClientRequestDto {


   private Set<Long> organizationTypeIds;
    private Set<Long> organizationSubTypeIds;

    private Set<Long> organizationServiceIds;

    private Set<Long> organizationSubServiceIds;

    public Set<Long> getOrganizationTypeIds() {
        return organizationTypeIds;
    }

    public void setOrganizationTypeIds(Set<Long> organizationTypeIds) {
        this.organizationTypeIds = organizationTypeIds;
    }

    public Set<Long> getOrganizationSubTypeIds() {
        return organizationSubTypeIds;
    }

    public void setOrganizationSubTypeIds(Set<Long> organizationSubTypeIds) {
        this.organizationSubTypeIds = organizationSubTypeIds;
    }

    public Set<Long> getOrganizationServiceIds() {
        return organizationServiceIds;
    }

    public void setOrganizationServiceIds(Set<Long> organizationServiceIds) {
        this.organizationServiceIds = organizationServiceIds;
    }

    public Set<Long> getOrganizationSubServiceIds() {
        return organizationSubServiceIds;
    }

    public void setOrganizationSubServiceIds(Set<Long> organizationSubServiceIds) {
        this.organizationSubServiceIds = organizationSubServiceIds;
    }

    public OrganizationTypeAndServiceRestClientRequestDto()
    {


    }

}
