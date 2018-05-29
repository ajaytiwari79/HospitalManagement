package com.kairos.persistence.model.organization;

import java.util.List;

/**
 * Created by vipul on 8/9/17.
 */
public class OrganizationTypeAndSubTypeDTO {
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private Long unitId;
    boolean isParent=false;
    private Long parentOrganizationId;
    private Long countryId;

    public OrganizationTypeAndSubTypeDTO() {
        //Default Constructor
    }

    public OrganizationTypeAndSubTypeDTO(List<Long> organizationTypes, List<Long> organizationSubTypes, Long countryId) {
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.countryId = countryId;
    }

    public Long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public boolean isParent() {

        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
