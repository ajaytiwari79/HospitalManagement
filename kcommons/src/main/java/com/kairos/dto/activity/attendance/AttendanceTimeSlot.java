package com.kairos.dto.activity.attendance;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class AttendanceTimeSlot {


    private LocalDateTime from;
    private LocalDateTime to;
    private Long unitId;
    private Long clockInReasonCode;
    private Long clockOutReasonCode;
    private Long unitPositionId;
    private BigInteger shiftId;
    private boolean systemGeneratedClockOut;

    public AttendanceTimeSlot() {
    }

    public AttendanceTimeSlot(LocalDateTime from, Long clockInReasonCode, Long unitPositionId,Long unitId) {
        this.from = from;
        this.clockInReasonCode = clockInReasonCode;
        this.unitPositionId = unitPositionId;
        this.unitId=unitId;
    }

    public AttendanceTimeSlot(LocalDateTime from, Long unitId, Long clockInReasonCode, Long unitPositionId, BigInteger shiftId) {
        this.from = from;
        this.unitId = unitId;
        this.clockInReasonCode = clockInReasonCode;
        this.unitPositionId = unitPositionId;
        this.shiftId = shiftId;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
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

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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
