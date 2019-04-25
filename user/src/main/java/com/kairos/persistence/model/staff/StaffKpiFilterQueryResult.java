package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffKpiFilterQueryResult {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Long> unitIds;
    private Long unitId;
    private String unitName;
    private List<EmploymentQueryResult> employment;

    public StaffKpiFilterQueryResult() {
        //Default Constructor
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

    public List<EmploymentQueryResult> getEmployment() {
        return employment;
    }

    public void setEmployment(List<EmploymentQueryResult> employment) {
        this.employment = employment;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }
}
