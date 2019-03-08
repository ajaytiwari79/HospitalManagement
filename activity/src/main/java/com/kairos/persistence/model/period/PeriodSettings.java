package com.kairos.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;

/**
 * Created by prerna on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodSettings extends MongoBaseEntity {

    private int presenceLimitInYear;
    private int absenceLimitInYear;
    private Long unitId;
//    private int duration;
//    private DurationType durationType;

    public PeriodSettings(){
        // default constructor
    }

    public PeriodSettings(int presenceLimitInYear, int absenceLimitInYear, Long unitId){
        this.presenceLimitInYear = presenceLimitInYear;
        this.absenceLimitInYear = absenceLimitInYear;
        this.unitId = unitId;
//        this.duration = duration;
//        this.durationType = durationType;
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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    /*public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public DurationType getPayrollFrequency() {
        return durationType;
    }
    public void setPayrollFrequency(DurationType durationType) {
        this.durationType = durationType;
    }*/
}
