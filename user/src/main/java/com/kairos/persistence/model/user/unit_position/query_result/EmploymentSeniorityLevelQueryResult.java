package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.EmploymentLine;
import com.kairos.persistence.model.user.unit_position.EmploymentLineEmploymentTypeRelationShip;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class EmploymentSeniorityLevelQueryResult {

    private Long employmentId;
    private SeniorityLevel seniorityLevel;
    private EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip;
    private EmploymentType employmentType;
    private EmploymentLine employmentLine;

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }
    public EmploymentLineEmploymentTypeRelationShip getEmploymentLineEmploymentTypeRelationShip() {
        return employmentLineEmploymentTypeRelationShip;
    }

    public void setEmploymentLineEmploymentTypeRelationShip(EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip) {
        this.employmentLineEmploymentTypeRelationShip = employmentLineEmploymentTypeRelationShip;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public EmploymentLine getEmploymentLine() {
        return employmentLine;
    }

    public void setEmploymentLine(EmploymentLine employmentLine) {
        this.employmentLine = employmentLine;
    }
}
