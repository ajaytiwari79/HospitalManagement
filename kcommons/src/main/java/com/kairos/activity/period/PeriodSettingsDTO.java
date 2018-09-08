package com.kairos.activity.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

/**
 * Created by prerna on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodSettingsDTO {
    private BigInteger id;
    private Long unitId;
    private Long parentOrgId;
    // upto when presence shift can be entered for request phase only
    private int presenceLimitInYear;
    // upto when absence shift can be entered for request phase only
    private int absenceLimitInYear;

    public PeriodSettingsDTO(){
        // default constructor
    }

    public PeriodSettingsDTO(BigInteger id, Long unitId, int presenceLimitInYear, int absenceLimitInYear) {
        this.id = id;
        this.unitId = unitId;
        this.presenceLimitInYear = presenceLimitInYear;
        this.absenceLimitInYear = absenceLimitInYear;

    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getPresenceLimitInYear() {
        return presenceLimitInYear;
    }

    public void setPresenceLimitInYear(int presenceLimitInYear) {
        this.presenceLimitInYear = presenceLimitInYear;
    }

    public int getAbsenceLimitInYear() {
        return absenceLimitInYear;
    }

    public void setAbsenceLimitInYear(int absenceLimitInYear) {
        this.absenceLimitInYear = absenceLimitInYear;
    }


}
