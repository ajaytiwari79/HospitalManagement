package com.kairos.dto.activity.night_worker;

import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
public class ExpertiseNightWorkerSettingDTO {
    private BigInteger id;
    private TimeSlot timeSlot;
    private Integer minMinutesToCheckNightShift;
    private DurationType intervalUnitToCheckNightWorker;
    private Integer intervalValueToCheckNightWorker;
    private Integer minShiftsValueToCheckNightWorker;
    private DisplayUnit minShiftsUnitToCheckNightWorker;
    private Long countryId;
    private Long expertiseId;


    public ExpertiseNightWorkerSettingDTO(TimeSlot timeSlot, Integer minMinutesToCheckNightShift, DurationType intervalUnitToCheckNightWorker, Integer intervalValueToCheckNightWorker,
                                       Integer minShiftsValueToCheckNightWorker, DisplayUnit minShiftsUnitToCheckNightWorker, Long countryId, Long expertiseId){
        this.timeSlot = timeSlot;
        this.minMinutesToCheckNightShift = minMinutesToCheckNightShift;
        this.intervalUnitToCheckNightWorker = intervalUnitToCheckNightWorker;
        this.intervalValueToCheckNightWorker = intervalValueToCheckNightWorker;
        this.minShiftsValueToCheckNightWorker = minShiftsValueToCheckNightWorker;
        this.minShiftsUnitToCheckNightWorker = minShiftsUnitToCheckNightWorker;
        this.countryId = countryId;
        this.expertiseId = expertiseId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public DisplayUnit getMinShiftsUnitToCheckNightWorker() {
        return minShiftsUnitToCheckNightWorker;
    }

    public void setMinShiftsUnitToCheckNightWorker(DisplayUnit minShiftsUnitToCheckNightWorker) {
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

    public String toString() {
        return "ExpertiseNightWorkerSettingDTO{" +
                "id=" + id +
                ", timeSlot=" + timeSlot +
                ", minMinutesToCheckNightShift=" + minMinutesToCheckNightShift +
                ", intervalUnitToCheckNightWorker=" + intervalUnitToCheckNightWorker +
                ", intervalValueToCheckNightWorker=" + intervalValueToCheckNightWorker +
                ", minShiftsValueToCheckNightWorker=" + minShiftsValueToCheckNightWorker +
                ", minShiftsUnitToCheckNightWorker=" + minShiftsUnitToCheckNightWorker +
                ", countryId=" + countryId +
                ", expertiseId=" + expertiseId +
                '}';
    }
}
