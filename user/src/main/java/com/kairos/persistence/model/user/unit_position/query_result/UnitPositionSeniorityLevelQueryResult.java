package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UnitPositionSeniorityLevelQueryResult {

    private UnitPosition unitPosition;
    private SeniorityLevel seniorityLevel;
    private UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip;
    private EmploymentType employmentType;

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
    public UnitPositionEmploymentTypeRelationShip getUnitPositionEmploymentTypeRelationShip() {
        return unitPositionEmploymentTypeRelationShip;
    }

    public void setUnitPositionEmploymentTypeRelationShip(UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip) {
        this.unitPositionEmploymentTypeRelationShip = unitPositionEmploymentTypeRelationShip;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }



}
