package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffKpiFilterQueryResult {
    private Long id;
    private String firstName;
    private String lastName;
    private Long unitId;
    private List<UnitPositionQueryResult> unitPosition;

    public StaffKpiFilterQueryResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<UnitPositionQueryResult> getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(List<UnitPositionQueryResult> unitPosition) {
        this.unitPosition = unitPosition;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
