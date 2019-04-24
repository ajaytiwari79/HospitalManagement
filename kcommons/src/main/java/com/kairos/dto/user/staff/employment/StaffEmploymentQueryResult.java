package com.kairos.dto.user.staff.employment;


public class StaffEmploymentQueryResult {

    private Long staffId;
    private String staffEmail;
    private Long employmentId;
    private Integer workingDaysPerWeek;
    private Integer totalWeeklyHours;
    private Integer contractedMinByWeek;
    private Long startDate;
    private Long endDate;
    private Integer accumulatedTimeBank;
    private Integer deltaWeeklytimeBank;

    public Integer getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(Integer totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }
    public Integer getAccumulatedTimeBank() {
        return accumulatedTimeBank;
    }

    public void setAccumulatedTimeBank(Integer accumulatedTimeBank) {
        this.accumulatedTimeBank = accumulatedTimeBank;
    }

    public Integer getDeltaWeeklytimeBank() {
        return deltaWeeklytimeBank;
    }

    public void setDeltaWeeklytimeBank(Integer deltaWeeklytimeBank) {
        this.deltaWeeklytimeBank = deltaWeeklytimeBank;
    }

    public Integer getPlannedHoursWeek() {
        return plannedHoursWeek;
    }

    public void setPlannedHoursWeek(Integer plannedHoursWeek) {
        this.plannedHoursWeek = plannedHoursWeek;
    }

    private Integer plannedHoursWeek;

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


    public String toString() {
    return this.staffId+" ---- "+this.staffEmail;
    }
}
