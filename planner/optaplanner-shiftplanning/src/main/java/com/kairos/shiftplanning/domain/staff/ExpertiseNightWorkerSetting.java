package com.kairos.shiftplanning.domain.staff;

import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.DurationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertiseNightWorkerSetting {
    private BigInteger id;
    private TimeSlot timeSlot;
    private int minMinutesToCheckNightShift;
    private DurationType intervalUnitToCheckNightWorker;
    private int intervalValueToCheckNightWorker;
    private int minShiftsValueToCheckNightWorker;
    private XAxisConfig minShiftsUnitToCheckNightWorker;
    private Long expertiseId;

}
