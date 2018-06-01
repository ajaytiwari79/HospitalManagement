package com.kairos.activity.response.dto.counter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
