package com.kairos.persistence.model.staff.personal_details;

import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.employment.Employment;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 6/2/18.
 */
@QueryResult
public class OrganizationStaffWrapper {
    private Unit unit;
    private Staff staff;
    private Employment employment;

    public OrganizationStaffWrapper() {
        //Default Constructor
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Employment getEmployment() {
        return employment;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
    }
}
