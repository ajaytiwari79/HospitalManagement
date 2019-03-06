package com.kairos.persistence.model.staff.position;

import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

import java.util.List;

/**
 * Created by yatharth on 13/4/18.
 */
public class EmploymentUnitPositionDTO {

    private PositionQueryResult position;
    private List<UnitPositionQueryResult> unitPositions;

    public PositionQueryResult getPosition() {
        return position;
    }

    public EmploymentUnitPositionDTO() {

    }

    public EmploymentUnitPositionDTO(PositionQueryResult position, List<UnitPositionQueryResult> unitPositions) {
        this.position = position;
        this.unitPositions = unitPositions;

    }

    public void setPosition(PositionQueryResult position) {
        this.position = position;
    }

    public List<UnitPositionQueryResult> getUnitPositions() {
        return unitPositions;
    }

    public void setUnitPositions(List<UnitPositionQueryResult> unitPositions) {
        this.unitPositions = unitPositions;
    }



}
