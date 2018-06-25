package com.planner.domain.staff;

import com.planner.domain.MongoBaseEntity;
import com.planner.enums.ShiftType;
import org.joda.time.DateTime;
//import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;

//@Table
public class PlanningShift extends MongoBaseEntity {

    private String staffId;
    private Date startTime;
    private Date endTime;
    private ShiftType shiftType;

    public PlanningShift() {
    }

    public PlanningShift(String staffId, Date startTime, Date endTime) {
        this.staffId = staffId;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Date getStartTime() {
        return startTime;
    }
    public DateTime getStartDate(){
         return new DateTime(startTime);
    }

    public DateTime getEndDate(){
        return new DateTime(endTime);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}
