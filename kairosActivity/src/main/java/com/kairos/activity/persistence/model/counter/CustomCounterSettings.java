package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;
import com.kairos.activity.persistence.enums.counter.CounterView;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CustomCounterSettings extends MongoBaseEntity{

    private BigInteger staffId;
    private BigInteger couterAccessibilityId;
    private int order;
    private boolean configured = false;
    private CounterView viewDefault;
    private CounterLevel level;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public BigInteger getStaffId() {
        return staffId;
    }

    public void setStaffId(BigInteger staffId) {
        this.staffId = staffId;
    }

    public BigInteger getCouterAccessibilityId() {
        return couterAccessibilityId;
    }

    public void setCouterAccessibilityId(BigInteger couterAccessibilityId) {
        this.couterAccessibilityId = couterAccessibilityId;
    }
}
