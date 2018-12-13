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

    public List<UnitPositionQueryResult> getUnitPositionList() {
        return unitPositionList;
    }

    public void setUnitPositionList(List<UnitPositionQueryResult> unitPositionList) {
        this.unitPositionList = unitPositionList;
    }
}
