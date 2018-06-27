package com.kairos.user.organization;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationRequestWrapper {

    OrganizationDTO company;
    OrganizationDTO workCenterUnit;
    OrganizationDTO gdprUnit;

    public OrganizationDTO getCompany() {
        return company;
    }

    public void setCompany(OrganizationDTO company) {
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
