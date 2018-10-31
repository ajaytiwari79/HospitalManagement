package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.UnitPositionLine;
import com.kairos.persistence.model.user.unit_position.UnitPositionLineEmploymentTypeRelationShip;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UnitPositionSeniorityLevelQueryResult {

    private Long unitPositionId;
    private SeniorityLevel seniorityLevel;
    private UnitPositionLineEmploymentTypeRelationShip unitPositionLineEmploymentTypeRelationShip;
    private EmploymentType employmentType;
    private UnitPositionLine unitPositionLine;

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }
    public UnitPositionLineEmploymentTypeRelationShip getUnitPositionLineEmploymentTypeRelationShip() {
        return unitPositionLineEmploymentTypeRelationShip;
    }

    public void setUnitPositionLineEmploymentTypeRelationShip(UnitPositionLineEmploymentTypeRelationShip unitPositionLineEmploymentTypeRelationShip) {
        this.unitPositionLineEmploymentTypeRelationShip = unitPositionLineEmploymentTypeRelationShip;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public UnitPositionLine getUnitPositionLine() {
        return unitPositionLine;
    }

    public void setUnitPositionLine(UnitPositionLine unitPositionLine) {
        this.unitPositionLine = unitPositionLine;
    }
}
