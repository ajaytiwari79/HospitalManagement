package com.kairos.activity.component.counter;

import com.kairos.activity.enums.counter.CounterType;
import com.kairos.activity.service.counter.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


@Component
public class CounterServiceMapping {
    @Inject
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
