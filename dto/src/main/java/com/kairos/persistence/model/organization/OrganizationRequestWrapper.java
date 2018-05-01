package com.kairos.persistence.model.organization;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationRequestWrapper {

    ParentOrganizationDTO company;
    OrganizationDTO workCenterUnit;
    OrganizationDTO gdprUnit;

    public ParentOrganizationDTO getCompany() {
        return company;
    }

    public void setCompany(ParentOrganizationDTO company) {
        this.company = company;
    }

    public OrganizationDTO getWorkCenterUnit() {
        return workCenterUnit;
    }

    public void setWorkCenterUnit(OrganizationDTO workCenterUnit) {
        this.workCenterUnit = workCenterUnit;
    }

    public OrganizationDTO getGdprUnit() {
        return gdprUnit;
    }

    public void setGdprUnit(OrganizationDTO gdprUnit) {
        this.gdprUnit = gdprUnit;
    }
}
