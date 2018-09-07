package com.kairos.persistence.model.break_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class BreakSettings extends MongoBaseEntity {
    private Long unitId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long numberOfBreaks;
    private BigInteger paidActivityId;
    private BigInteger unpaidActivityId;

    public BreakSettings() {
        //Default Constructor
    }

    public BreakSettings(Long unitId, Long shiftDurationInMinute, Long breakDurationInMinute, Long numberOfBreaks) {
        this.unitId = unitId;
        this.shiftDurationInMinute = shiftDurationInMinute;
        this.breakDurationInMinute = breakDurationInMinute;
        this.numberOfBreaks = numberOfBreaks;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getShiftDurationInMinute() {
        return shiftDurationInMinute;
    }

    public void setShiftDurationInMinute(Long shiftDurationInMinute) {
        this.shiftDurationInMinute = shiftDurationInMinute;
    }

    public Long getBreakDurationInMinute() {
        return breakDurationInMinute;
    }

    public void setBreakDurationInMinute(Long breakDurationInMinute) {
        this.breakDurationInMinute = breakDurationInMinute;
    }

    public Long getNumberOfBreaks() {
        return numberOfBreaks;
    }

    public void setNumberOfBreaks(Long numberOfBreaks) {
        this.numberOfBreaks = numberOfBreaks;
    }

    public BigInteger getPaidActivityId() {
        return paidActivityId;
    }

    public void setPaidActivityId(BigInteger paidActivityId) {
        this.paidActivityId = paidActivityId;
    }

    public BigInteger getUnpaidActivityId() {
        return unpaidActivityId;
    }

    public void setUnpaidActivityId(BigInteger unpaidActivityId) {
        this.unpaidActivityId = unpaidActivityId;
    }
}
