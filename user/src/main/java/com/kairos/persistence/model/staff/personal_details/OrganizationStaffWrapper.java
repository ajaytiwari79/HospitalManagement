package com.kairos.persistence.model.staff.personal_details;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 6/2/18.
 */
@QueryResult
public class OrganizationStaffWrapper {
    private Organization organization;
    private Staff staff;
    private UnitPosition unitPosition;

    public OrganizationStaffWrapper() {
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public UnitPosition getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(UnitPosition unitPosition) {
        this.unitPosition = unitPosition;
    }
}
