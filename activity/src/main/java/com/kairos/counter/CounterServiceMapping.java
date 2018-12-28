package com.kairos.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.service.counter.ContractualAndPlannedHoursCalculationService;
import com.kairos.service.counter.CounterService;
import com.kairos.service.counter.PlannedHoursCalculationService;
import com.kairos.service.counter.RestingHoursCalculationService;
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
        this.counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, restingHoursCalculationService);
        logger.info("Enum mapping for resting hours: "+this.counters);
    }

    @Inject
    public void calculatePlannedHours(PlannedHoursCalculationService plannedHoursCalculationService) {
        this.counters.put(CounterType.PLANNED_HOURS, plannedHoursCalculationService);
        logger.info("Enum mapping for planned hours: "+this.counters);
    }

    @Inject
    public void calculateContractualAndPlannedHours(ContractualAndPlannedHoursCalculationService contractualAndPlannedHoursCalculationService) {
        this.counters.put(CounterType.CONTRACTUAL_AND_PLANNED_HOURS, contractualAndPlannedHoursCalculationService);
        logger.info("Enum mapping for contractual and planned hours : "+this.counters);
    }

    public CounterService getService(CounterType counterType){
        return (CounterService) this.counters.get(counterType);
    }
}
