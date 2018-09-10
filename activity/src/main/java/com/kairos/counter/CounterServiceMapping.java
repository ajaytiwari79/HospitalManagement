package com.kairos.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.service.counter.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Component
public class CounterServiceMapping {
    //@Inject
    private final static Logger logger = LoggerFactory.getLogger(CounterServiceMapping.class);
    private Map counters = null;
    private CounterServiceMapping(){
    }

    private void prepareStore(){
        if(counters != null) return;
        counters = new HashMap<CounterType, CounterService>();
       // counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, xCounterService);
    }

    public CounterService getService(CounterType counterType){
        this.prepareStore();
        //logger.debug("delta: "+(CounterService) this.counters.get(counterType));
        return (CounterService) this.counters.get(counterType);
    }
}
