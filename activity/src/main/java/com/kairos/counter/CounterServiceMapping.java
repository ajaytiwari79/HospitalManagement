package com.kairos.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.service.counter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Component
public class CounterServiceMapping {
    private final static Logger logger = LoggerFactory.getLogger(CounterServiceMapping.class);
    private Map<CounterType, CounterService> counters = new HashMap();

    @Inject
    public void setCounterService(RestingHoursCalculationService restingHoursCalculationService) {
        logger.info("Enum mapping for resting hours: "+this.counters);
        this.counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, restingHoursCalculationService);
    }

    @Inject
    public void calculatePlannedHours(PlannedHoursCalculationService plannedHoursCalculationService) {
        logger.info("Enum mapping for planned hours: "+this.counters);
        this.counters.put(CounterType.PLANNED_HOURS, plannedHoursCalculationService);
    }

    @Inject
    public void calculateContractualAndPlannedHours(ContractualAndPlannedHoursCalculationService contractualAndPlannedHoursCalculationService) {
        logger.info("Enum mapping for contractual and planned hours : "+this.counters);
        this.counters.put(CounterType.CONTRACTUAL_AND_PLANNED_HOURS, contractualAndPlannedHoursCalculationService);
    }

    @Inject
    public void calculateTimeBankForUnit(TimeBankKpiCalculationService timeBankKpiCalculationService) {
        logger.info("Enum mapping for time bank for unit : "+this.counters);
        this.counters.put(CounterType.TIMEBANK, timeBankKpiCalculationService);
    }

    @Inject
    public void durationOfShiftAndActivity(ShiftAndActivityDurationKpiService shiftAndActivityKpiService) {
        logger.info("Enum mapping for Duration of shift and activity : "+this.counters);
        this.counters.put(CounterType.SHIFT_AND_ACTIVITY_DURATION, shiftAndActivityKpiService);
    }

    @Inject
    public void calculateWeekDayAndWeekEndHours(WeekDayAndWeekEndHoursKpiService weekDayAndWeekEndHoursKpiService) {
        logger.info("Enum mapping for calculate WeekDay And WeekEnd Hours : "+this.counters);
        this.counters.put(CounterType.WEEKDAY_AND_WEEKEND_HOURS, weekDayAndWeekEndHoursKpiService);
    }


    public CounterService getService(CounterType counterType){
        return (CounterService) this.counters.get(counterType);
    }
}
