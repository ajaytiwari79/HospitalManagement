package com.kairos.activity.response.dto;

import com.kairos.activity.client.dto.client.AbsenceTypes;
import com.kairos.activity.client.dto.organization.OrganizationContactAddress;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.client.dto.staff.StaffDTO;

/**
 * Created by prabjot on 30/8/17.
 */
public class TimeCareOrganizationDTO {

    private OrganizationContactAddress organizationContactAddress;
    private StaffDTO staff;
    private AbsenceTypes absenceTypes;
    private OrganizationDTO organization;

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public OrganizationContactAddress getOrganizationContactAddress() {
        return organizationContactAddress;
    }

    public void setOrganizationContactAddress(OrganizationContactAddress organizationContactAddress) {
        this.organizationContactAddress = organizationContactAddress;
    }

    public StaffDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    public AbsenceTypes getAbsenceTypes() {
        return absenceTypes;
    }

    public void setAbsenceTypes(AbsenceTypes absenceTypes) {
        this.absenceTypes = absenceTypes;
    }
}
