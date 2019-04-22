package com.kairos.dto.user.staff;

import com.kairos.dto.user.organization.OrganizationDTO;

/**
 * Created by vipul on 6/2/18.
 */
public class OrganizationStaffWrapper {
    private OrganizationDTO organization;
    private StaffDTO staff;
    private UnitPositionDTO unitPosition;

    public OrganizationStaffWrapper() {
        //Default Constructor
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public StaffDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    public UnitPositionDTO getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(UnitPositionDTO unitPosition) {
        this.unitPosition = unitPosition;
    }
}
