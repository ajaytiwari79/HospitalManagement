package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class StaffUnitPositionQueryResult {

    private Long staffId;
    private Long unitPositionId;
    private Integer workingDaysPerWeek;
    private Integer contractedMinByWeek;
    private Long startDate;
    private Long endDate;

    public StaffUnitPositionQueryResult() {
        // dc
    }

    public Integer getWorkingDaysPerWeek() {
        return workingDaysPerWeek;
    }

    public void setWorkingDaysPerWeek(Integer workingDaysPerWeek) {
        this.workingDaysPerWeek = workingDaysPerWeek;
    }

    public Integer getContractedMinByWeek() {
        return contractedMinByWeek;
    }

    public void setContractedMinByWeek(Integer contractedMinByWeek) {
        this.contractedMinByWeek = contractedMinByWeek;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }



    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}
