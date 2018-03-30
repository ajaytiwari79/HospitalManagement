package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 1/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class StaffUnitPositionWrapper {
    private Staff staff;
    private UnitPosition unitPosition;


    public StaffUnitPositionWrapper() {
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
