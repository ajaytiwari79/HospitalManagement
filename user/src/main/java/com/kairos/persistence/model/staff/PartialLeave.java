package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prabjot on 24/10/16.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartialLeave extends UserBaseEntity {

    private long startDate;
    private long endDate;
    private LeaveType leaveType;
    private float amount;
    private String employmentId;
    private String note;
    public PartialLeave() {
        //Default Constructor
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setEmploymentId(String employmentId) {
        this.employmentId = employmentId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public float getAmount() {
        return amount;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public String getNote() {
        return note;
    }

    public enum LeaveType {
        EMERGENCY_LEAVE("Emergency leave"), HOLIDAY_LEAVE("Holiday leave");
        public String value;

        LeaveType(String value) {
            this.value = value;
        }

        public LeaveType getByValue(String value) {
            for (LeaveType leaveType : LeaveType.values()) {
                if (leaveType.value.equals(value)) {
                    return leaveType;
                }
            }
            return null;
        }
    }

}
