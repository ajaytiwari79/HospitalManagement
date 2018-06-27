package com.kairos.persistence.model.break_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BreakSettings extends MongoBaseEntity {
    private Long unitId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long numberOfBreaks;

    public BreakSettings() {
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

    public BreakSettings(Long unitId, Long shiftDurationInMinute, Long breakDurationInMinute, Long numberOfBreaks) {
        this.unitId = unitId;
        this.shiftDurationInMinute = shiftDurationInMinute;
        this.breakDurationInMinute = breakDurationInMinute;
        this.numberOfBreaks = numberOfBreaks;
    }
}
