package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.enums.counter.CounterType;

import java.util.List;

public class CounterDTO {
    private List<CounterType> counters;

    private CounterFilterDTO filter;

    public List<CounterType> getCounters() {
        return counters;
    }

    public void setCounters(List<CounterType> counters) {
        this.counters = counters;
    }

    public CounterFilterDTO getFilter() {
        return filter;
    }

    public void setFilter(CounterFilterDTO filter) {
        this.filter = filter;
    }
}
