package com.kairos.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.service.counter.CounterService;
import com.kairos.service.counter.RestingHoursCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Inject
    RestingHoursCalculationService restingHoursCalculationService;

    private final static Logger logger = LoggerFactory.getLogger(CounterServiceMapping.class);
    private Map<CounterType, CounterService> counters = new HashMap();

    @Autowired
    public void setCounterService(RestingHoursCalculationService restingHoursCalculationService) {
        this.counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, restingHoursCalculationService);
        System.out.println("Enum mapping: "+this.counters);
    }




//    private void prepareStore(){
//        if(counters != null) return;
//        counters = new HashMap<CounterType, CounterService>();
//       // counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, xCounterService);
//    }

    public CounterService getService(CounterType counterType){
//        this.prepareStore();
        //logger.debug("delta: "+(CounterService) this.counters.get(counterType));
        return (CounterService) this.counters.get(counterType);
    }
}
