package com.kairos.activity.break_settings;

import javax.validation.constraints.Min;
import java.math.BigInteger;

public class BreakSettingsDTO {
    private Long unitId;
    @Min(value = 1, message = "shift duration cant be zero")
    private Long shiftDurationInMinute;
    @Min(value = 1, message = "Break duration cant be zero")
    private Long breakDurationInMinute;
    private Long numberOfBreaks;
    private BigInteger id;

    public BreakSettingsDTO() {
        //
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BreakSettingsDTO(Long unitId, Long shiftDurationInMinute, Long breakDurationInMinute, Long numberOfBreaks) {
        this.unitId = unitId;
        this.shiftDurationInMinute = shiftDurationInMinute;
        this.breakDurationInMinute = breakDurationInMinute;
        this.numberOfBreaks = numberOfBreaks;
    }
}
