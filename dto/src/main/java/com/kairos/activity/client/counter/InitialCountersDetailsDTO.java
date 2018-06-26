package com.kairos.activity.client.counter;

import java.util.List;

public class InitialCountersDetailsDTO {
    private List<RefCounterDefDTO> counterDefs;
    private List<CounterOrderDTO> orderedList;

    public List<CounterOrderDTO> getOrderedList() {
        return orderedList;
    }

    public void setOrderedList(List<CounterOrderDTO> orderedList) {
        this.orderedList = orderedList;
    }

    public List<RefCounterDefDTO> getCounterDefs() {
        return counterDefs;
    }

    public void setCounterDefs(List<RefCounterDefDTO> counterDefs) {
        this.counterDefs = counterDefs;
    }
}
