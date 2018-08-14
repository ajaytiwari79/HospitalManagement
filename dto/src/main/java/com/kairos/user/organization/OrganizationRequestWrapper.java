package com.kairos.user.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationRequestWrapper {

    private OrganizationBasicDTO company;
    private OrganizationBasicDTO workCenterUnit;
    private OrganizationBasicDTO gdprUnit;
    private List<OrganizationBasicDTO> units;

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

    public List<OrganizationBasicDTO> getUnits() {
        return units=Optional.ofNullable(units).orElse(new ArrayList<>());
    }

    public void setUnits(List<OrganizationBasicDTO> units) {
        this.units = units;
    }
}
