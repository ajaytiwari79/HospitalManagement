package com.kairos.dto.activity.unit_settings.activity_configuration;

import java.math.BigInteger;

public class AbsencePlannedTime {
    private BigInteger phaseId;
    private BigInteger timeTypeId;  // if exception is true then time type is null
    private BigInteger plannedTimeId;
    private boolean exception;

    public AbsencePlannedTime() {
        // DC
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public AbsencePlannedTime(BigInteger phaseId,  BigInteger plannedTimeId, boolean exception) {
        this.phaseId = phaseId;
        this.plannedTimeId = plannedTimeId;
        this.exception = exception;
    }

    public AbsencePlannedTime(BigInteger phaseId, BigInteger timeTypeId, BigInteger plannedTimeId, boolean exception) {
        this.phaseId = phaseId;
        this.timeTypeId = timeTypeId;
        this.plannedTimeId = plannedTimeId;
        this.exception = exception;
    }
}
