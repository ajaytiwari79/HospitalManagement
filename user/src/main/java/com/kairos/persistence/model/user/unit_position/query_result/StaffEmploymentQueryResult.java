package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/*
 *Created By Pavan on 7/12/18
 *
 */
@QueryResult
public class StaffEmploymentQueryResult {
    private Staff staff;
    private Employment employment;
    private List<UnitPositionQueryResult> unitPositionList;

    public StaffEmploymentQueryResult() {
        //Default Constructor
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

    public List<UnitPositionQueryResult> getUnitPositionList() {
        return unitPositionList;
    }

    public void setUnitPositionList(List<UnitPositionQueryResult> unitPositionList) {
        this.unitPositionList = unitPositionList;
    }
}
