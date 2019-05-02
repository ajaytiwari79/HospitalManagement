package com.kairos.persistence.model.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class StaffEmploymentQueryResult {

    private Long staffId;
    private String staffEmail;
    private Long employmentId;
    private Integer workingDaysPerWeek;
    private Integer contractedMinByWeek;
    private Long startDate;
    private Long endDate;

    public StaffEmploymentQueryResult() {
        // dc
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
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

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }
}
