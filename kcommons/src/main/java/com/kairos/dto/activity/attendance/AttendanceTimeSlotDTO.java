package com.kairos.dto.activity.attendance;

import java.math.BigInteger;
import java.util.Date;

public class AttendanceTimeSlotDTO {

    private Date from;
    private Date to;
    private Long unitId;
    private Long clockInReasonCode;
    private Long clockOutReasonCode;
    private Long employmentId;
    private BigInteger shiftId;
    private boolean systemGeneratedClockOut;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getClockInReasonCode() {
        return clockInReasonCode;
    }

    public void setClockInReasonCode(Long clockInReasonCode) {
        this.clockInReasonCode = clockInReasonCode;
    }

    public Long getClockOutReasonCode() {
        return clockOutReasonCode;
    }

    public void setClockOutReasonCode(Long clockOutReasonCode) {
        this.clockOutReasonCode = clockOutReasonCode;
    }

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public boolean isSystemGeneratedClockOut() {
        return systemGeneratedClockOut;
    }

    public void setSystemGeneratedClockOut(boolean systemGeneratedClockOut) {
        this.systemGeneratedClockOut = systemGeneratedClockOut;
    }
}
