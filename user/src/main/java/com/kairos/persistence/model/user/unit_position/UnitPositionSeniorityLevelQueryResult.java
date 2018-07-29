package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UnitPositionSeniorityLevelQueryResult {

    private UnitPosition unitPosition;
    private SeniorityLevel seniorityLevel;

    public UnitPosition getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(UnitPosition unitPosition) {
        this.unitPosition = unitPosition;
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }


}
