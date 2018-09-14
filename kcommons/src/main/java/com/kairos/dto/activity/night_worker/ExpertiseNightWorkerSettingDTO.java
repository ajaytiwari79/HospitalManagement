package com.kairos.dto.activity.night_worker;

import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.DurationType;

public class ExpertiseNightWorkerSettingDTO {

    private TimeSlot timeSlot;
    private Integer minMinutesToCheckNightShift;
    private DurationType intervalUnitToCheckNightWorker;
    private Integer intervalValueToCheckNightWorker;
    private Integer minShiftsValueToCheckNightWorker;
    private CalculationUnit minShiftsUnitToCheckNightWorker;
    private Long countryId;
    private Long expertiseId;

    public ExpertiseNightWorkerSettingDTO(){
        // default constructor
    }

    public ExpertiseNightWorkerSettingDTO(TimeSlot timeSlot, Integer minMinutesToCheckNightShift, DurationType intervalUnitToCheckNightWorker, Integer intervalValueToCheckNightWorker,
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

    public DurationType getIntervalUnitToCheckNightWorker() {
        return intervalUnitToCheckNightWorker;
    }

    public void setIntervalUnitToCheckNightWorker(DurationType intervalUnitToCheckNightWorker) {
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
