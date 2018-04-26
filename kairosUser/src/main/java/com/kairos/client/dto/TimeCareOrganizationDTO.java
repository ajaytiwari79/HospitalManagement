package com.kairos.client.dto;

import com.kairos.persistence.model.organization.AbsenceTypes;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.user.staff.Staff;

/**
 * Created by prabjot on 30/8/17.
 */
public class TimeCareOrganizationDTO {

    private OrganizationContactAddress organizationContactAddress;
    private Staff staff;
    private AbsenceTypes absenceTypes;

    public OrganizationContactAddress getOrganizationContactAddress() {
        return organizationContactAddress;
    }

    public void setOrganizationContactAddress(OrganizationContactAddress organizationContactAddress) {
        this.organizationContactAddress = organizationContactAddress;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public AbsenceTypes getAbsenceTypes() {
        return absenceTypes;
    }

    public void setAbsenceTypes(AbsenceTypes absenceTypes) {
        this.absenceTypes = absenceTypes;
    }
}
