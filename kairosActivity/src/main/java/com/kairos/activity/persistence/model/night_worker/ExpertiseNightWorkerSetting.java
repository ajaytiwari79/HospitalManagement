package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.common.TimeSlot;
import com.kairos.persistence.model.enums.CalculationUnit;
import com.kairos.persistence.model.enums.DurationType;
import com.kairos.persistence.model.enums.IntervalUnit;

public class ExpertiseNightWorkerSetting extends MongoBaseEntity {

    private TimeSlot timeSlot;
    private Integer minMinutesToCheckNightShift;
    private IntervalUnit intervalUnitToCheckNightWorker;
    private Integer intervalValueToCheckNightWorker;
    private Integer minShiftsValueToCheckNightWorker;
    private CalculationUnit minShiftsUnitToCheckNightWorker;
    private Long countryId;
    private Long expertiseId;

    public ExpertiseNightWorkerSetting(){
        // default constructor
    }

    public ExpertiseNightWorkerSetting(TimeSlot timeSlot, Integer minMinutesToCheckNightShift, IntervalUnit intervalUnitToCheckNightWorker, Integer intervalValueToCheckNightWorker,
                                       Integer minShiftsValueToCheckNightWorker, CalculationUnit minShiftsUnitToCheckNightWorker, Long countryId, Long expertiseId){
        this.timeSlot = timeSlot;
        this.minMinutesToCheckNightShift = minMinutesToCheckNightShift;
        this.intervalUnitToCheckNightWorker = intervalUnitToCheckNightWorker;
        this.intervalValueToCheckNightWorker = intervalValueToCheckNightWorker;
        this.minShiftsValueToCheckNightWorker = minShiftsValueToCheckNightWorker;
        this.minShiftsUnitToCheckNightWorker = minShiftsUnitToCheckNightWorker;
        this.countryId = countryId;
        this.expertiseId = expertiseId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getMinMinutesToCheckNightShift() {
        return minMinutesToCheckNightShift;
    }

    public void setMinMinutesToCheckNightShift(Integer minMinutesToCheckNightShift) {
        this.minMinutesToCheckNightShift = minMinutesToCheckNightShift;
    }

    public IntervalUnit getIntervalUnitToCheckNightWorker() {
        return intervalUnitToCheckNightWorker;
    }

    public void setIntervalUnitToCheckNightWorker(IntervalUnit intervalUnitToCheckNightWorker) {
        this.intervalUnitToCheckNightWorker = intervalUnitToCheckNightWorker;
    }

    public Integer getIntervalValueToCheckNightWorker() {
        return intervalValueToCheckNightWorker;
    }

    public void setIntervalValueToCheckNightWorker(Integer intervalValueToCheckNightWorker) {
        this.intervalValueToCheckNightWorker = intervalValueToCheckNightWorker;
    }

    public Integer getMinShiftsValueToCheckNightWorker() {
        return minShiftsValueToCheckNightWorker;
    }

    public void setMinShiftsValueToCheckNightWorker(Integer minShiftsValueToCheckNightWorker) {
        this.minShiftsValueToCheckNightWorker = minShiftsValueToCheckNightWorker;
    }

    public CalculationUnit getMinShiftsUnitToCheckNightWorker() {
        return minShiftsUnitToCheckNightWorker;
    }

    public void setMinShiftsUnitToCheckNightWorker(CalculationUnit minShiftsUnitToCheckNightWorker) {
        this.minShiftsUnitToCheckNightWorker = minShiftsUnitToCheckNightWorker;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }
}
