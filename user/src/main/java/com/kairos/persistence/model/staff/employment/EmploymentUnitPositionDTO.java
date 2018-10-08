package com.kairos.persistence.model.staff.employment;

import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

import java.util.List;

/**
 * Created by yatharth on 13/4/18.
 */
public class EmploymentUnitPositionDTO {

    private EmploymentQueryResult employment;
    private List<UnitPositionQueryResult> unitPositions;

    public EmploymentQueryResult getEmployment() {
        return employment;
    }

    public EmploymentUnitPositionDTO() {

    }

    public EmploymentUnitPositionDTO(EmploymentQueryResult employment, List<UnitPositionQueryResult> unitPositions) {
        this.employment = employment;
        this.unitPositions = unitPositions;

    }

    public void setEmployment(EmploymentQueryResult employment) {
        this.employment = employment;
    }

    public List<UnitPositionQueryResult> getUnitPositions() {
        return unitPositions;
    }

    public void setUnitPositions(List<UnitPositionQueryResult> unitPositions) {
        this.unitPositions = unitPositions;
    }



}
