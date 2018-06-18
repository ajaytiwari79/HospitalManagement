package com.kairos.activity.constants;

import com.kairos.activity.enums.counter.CounterType;
import com.kairos.activity.service.counter.CounterService;
import com.kairos.activity.service.counter.XCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


@Component
public class CounterStore {
    @Inject
    XCounterService xCounterService;
    private final static Logger logger = LoggerFactory.getLogger(CounterStore.class);
    private Map counters = null;
    private CounterStore(){
    }

    private void prepareStore(){
        if(counters != null) return;
        counters = new HashMap<CounterType, CounterService>();
        counters.put(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, xCounterService);
    }

    public CounterService getService(CounterType counterType){
        this.prepareStore();
        //logger.debug("delta: "+(CounterService) this.counters.get(counterType));
        return (CounterService) this.counters.get(counterType);
    }
}
