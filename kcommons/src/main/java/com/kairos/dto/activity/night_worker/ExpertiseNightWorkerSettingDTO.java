package com.kairos.dto.activity.night_worker;

import com.kairos.dto.activity.counter.enums.XAxisConfig;
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
    private int minMinutesToCheckNightShift;
    private DurationType intervalUnitToCheckNightWorker;
    private int intervalValueToCheckNightWorker;
    private int minShiftsValueToCheckNightWorker;
    private XAxisConfig minShiftsUnitToCheckNightWorker;
    private Long countryId;
    private Long expertiseId;


    public ExpertiseNightWorkerSettingDTO(TimeSlot timeSlot, int minMinutesToCheckNightShift, DurationType intervalUnitToCheckNightWorker, int intervalValueToCheckNightWorker,
                                          int minShiftsValueToCheckNightWorker, XAxisConfig minShiftsUnitToCheckNightWorker, Long countryId, Long expertiseId){
        this.timeSlot = timeSlot;
        this.minMinutesToCheckNightShift = minMinutesToCheckNightShift;
        this.intervalUnitToCheckNightWorker = intervalUnitToCheckNightWorker;
        this.intervalValueToCheckNightWorker = intervalValueToCheckNightWorker;
        this.minShiftsValueToCheckNightWorker = minShiftsValueToCheckNightWorker;
        this.minShiftsUnitToCheckNightWorker = minShiftsUnitToCheckNightWorker;
        this.countryId = countryId;
        this.expertiseId = expertiseId;
    }


    @Override
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
