package com.planner.domain.staff;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.ShiftType;
import org.joda.time.DateTime;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;

@Table
public class PlanningShift extends BaseEntity {

    private String staffId;
    private Date startDateTime;
    private Date endDateTime;
    private ShiftType shiftType;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }
    public DateTime getStartDate(){
         return new DateTime(startDateTime);
    }

    public DateTime getEndDate(){
        return new DateTime(endDateTime);
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}
