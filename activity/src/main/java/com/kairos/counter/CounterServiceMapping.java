package com.kairos.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.service.counter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Component
public class CounterServiceMapping {
    private final static Logger LOGGER = LoggerFactory.getLogger(CounterServiceMapping.class);
    private Map<CounterType, CounterService> counters = new ConcurrentHashMap<>();

    @Inject
    public void setCounterService(RestingHoursCalculationService restingHoursCalculationService) {
        LOGGER.info("Enum mapping for resting hours: "+this.counters);
        this.counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, restingHoursCalculationService);
    }

    @Inject
    public void calculatePlannedHours(PlannedHoursCalculationService plannedHoursCalculationService) {
        LOGGER.info("Enum mapping for planned hours: "+this.counters);
        this.counters.put(CounterType.PLANNED_HOURS, plannedHoursCalculationService);
    }

    @Inject
    public void calculateContractualAndPlannedHours(ContractualAndPlannedHoursCalculationService contractualAndPlannedHoursCalculationService) {
        LOGGER.info("Enum mapping for contractual and planned hours : "+this.counters);
        this.counters.put(CounterType.CONTRACTUAL_AND_PLANNED_HOURS, contractualAndPlannedHoursCalculationService);
    }

    @Inject
    public void calculateTimeBankForUnit(TimeBankKpiCalculationService timeBankKpiCalculationService) {
        LOGGER.info("Enum mapping for time bank for unit : "+this.counters);
        this.counters.put(CounterType.TIMEBANK, timeBankKpiCalculationService);
    }

    @Inject
    public void durationOfShiftAndActivity(ShiftAndActivityDurationKpiService shiftAndActivityKpiService) {
        LOGGER.info("Enum mapping for Duration of shift and activity : "+this.counters);
        this.counters.put(CounterType.SHIFT_AND_ACTIVITY_DURATION, shiftAndActivityKpiService);
    }

    @Inject
    public void calculateDayTypeAndTimeSlotHours(DayTypeAndTimeSlotKpiService dayTypeAndTimeSlotKpiService) {
        LOGGER.info("Enum mapping for calculate daytype And timeslot Hours : "+this.counters);
        this.counters.put(CounterType.DAYTYPE_AND_TIMESLOT, dayTypeAndTimeSlotKpiService);
    }

    @Inject
    public void setCounterService(FibonacciKPIService fibonacciKPIService) {
        LOGGER.info("Enum mapping for resting hours: "+this.counters);
        this.counters.put(CounterType.FIBONACCI, fibonacciKPIService);
    }

    @Inject
    public void comparePlannedHoursVsTimeBank(PlannedHoursVsTimeBankService plannedHoursVsTimeBankService) {
        LOGGER.info("Enum mapping for planned hours and time bank : "+this.counters);
        this.counters.put(CounterType.PLANNED_HOURS_VS_TIMEBANK, plannedHoursVsTimeBankService);
    }

    @Inject
    public void setAbsencePlanningKPIService(AbsencePlanningKPIService absencePlanningKPIService) {
        LOGGER.info("Enum mapping for absences per interval : "+this.counters);
        this.counters.put(CounterType.ABSENCES_PER_INTERVAL, absencePlanningKPIService);
    }

    public CounterService getService(CounterType counterType){
        return (CounterService) this.counters.get(counterType);
    }
}
