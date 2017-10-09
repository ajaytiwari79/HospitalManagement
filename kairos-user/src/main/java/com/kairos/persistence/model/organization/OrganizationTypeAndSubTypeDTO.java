package com.kairos.persistence.model.organization;

import java.util.List;

/**
 * Created by vipul on 8/9/17.
 */
public class OrganizationTypeAndSubTypeDTO {
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private Long unitId;
    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
