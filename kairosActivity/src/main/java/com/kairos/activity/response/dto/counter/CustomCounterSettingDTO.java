package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;
import com.kairos.activity.persistence.enums.counter.CounterView;

import java.math.BigInteger;

public class CustomCounterSettingDTO {

    private BigInteger id;
    private CounterAccessiblityDTO counterAccessiblity;
    private CounterView viewDefault;
    private int order;
    private boolean configured = false;
    private CounterLevel level;

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public CounterLevel getLevel() {
        return level;
    }

    public void setLevel(CounterLevel level) {
        this.level = level;
    }

    public CounterView getViewDefault() {
        return viewDefault;
    }

    public void setViewDefault(CounterView viewDefault) {
        this.viewDefault = viewDefault;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public CounterAccessiblityDTO getCounterAccessiblity() {
        return counterAccessiblity;
    }

    public void setCounterAccessiblity(CounterAccessiblityDTO counterAccessiblity) {
        this.counterAccessiblity = counterAccessiblity;
    }
}
