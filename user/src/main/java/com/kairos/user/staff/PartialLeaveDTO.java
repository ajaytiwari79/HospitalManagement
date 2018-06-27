package com.kairos.user.staff;

/**
 * Created by prabjot on 23/2/17.
 */
public class PartialLeaveDTO {

    private String startDate;
    private String endDate;
    private float amount;
    private String employmentId;
    private String note;
    private PartialLeave.LeaveType leaveType;
    private Long id;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public String getNote() {
        return note;
    }

    public PartialLeave.LeaveType getLeaveType() {
        return leaveType;
    }

    public Long getId() {
        return id;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEmploymentId(String employmentId) {
        this.employmentId = employmentId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLeaveType(PartialLeave.LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
