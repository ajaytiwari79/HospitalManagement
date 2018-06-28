package com.kairos.user.organization;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationRequestWrapper {

    OrganizationBasicDTO company;
    OrganizationBasicDTO workCenterUnit;
    OrganizationBasicDTO gdprUnit;

    public OrganizationBasicDTO getCompany() {
        return company;
    }

    public void setCompany(OrganizationBasicDTO company) {
        this.company = company;
    }

    public OrganizationBasicDTO getWorkCenterUnit() {
        return workCenterUnit;
    }

    public void setWorkCenterUnit(OrganizationBasicDTO workCenterUnit) {
        this.workCenterUnit = workCenterUnit;
    }

    public OrganizationBasicDTO getGdprUnit() {
        return gdprUnit;
    }

    public void setGdprUnit(OrganizationBasicDTO gdprUnit) {
        this.gdprUnit = gdprUnit;
    }
}
