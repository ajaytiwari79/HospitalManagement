package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;

import java.math.BigInteger;
import java.util.List;

public class CounterAccessiblityUpdatorDTO {
    private BigInteger unitId;
    private CounterLevel counterLevel;
    private List<CounterModuleLinkDTO> counterModuleLinkDTOs;

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public CounterLevel getCounterLevel() {
        return counterLevel;
    }

    public void setCounterLevel(CounterLevel counterLevel) {
        this.counterLevel = counterLevel;
    }

    public List<CounterModuleLinkDTO> getCounterModuleLinkDTOs() {
        return counterModuleLinkDTOs;
    }

    public void setCounterModuleLinkDTOs(List<CounterModuleLinkDTO> counterModuleLinkDTOs) {
        this.counterModuleLinkDTOs = counterModuleLinkDTOs;
    }
}
