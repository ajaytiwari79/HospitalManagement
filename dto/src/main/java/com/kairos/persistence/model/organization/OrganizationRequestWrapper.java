package com.kairos.persistence.model.organization;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationRequestWrapper {

    ParentOrganizationDTO company;
    ParentOrganizationDTO workCenterUnit;
    ParentOrganizationDTO gdprUnit;

    public ParentOrganizationDTO getCompany() {
        return company;
    }

    public void setCompany(ParentOrganizationDTO company) {
        this.company = company;
    }

    public ParentOrganizationDTO getWorkCenterUnit() {
        return workCenterUnit;
    }

    public void setWorkCenterUnit(ParentOrganizationDTO workCenterUnit) {
        this.workCenterUnit = workCenterUnit;
    }

    public ParentOrganizationDTO getGdprUnit() {
        return gdprUnit;
    }

    public void setGdprUnit(ParentOrganizationDTO gdprUnit) {
        this.gdprUnit = gdprUnit;
    }
}
