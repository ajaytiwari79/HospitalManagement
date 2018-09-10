package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.FixedValueType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixedValue {
    private float amount;
    private Long currency;
    private Long currencyId;
    private FixedValueType type;

    public FixedValue() {
    }

    public FixedValue(float amount, Long currency, FixedValueType type) {
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Long getCurrency() {
        return currency;
    }

    public void setCurrency(Long currency) {
        this.currency = currency;
    }

    public FixedValueType getType() {
        return type;
    }

    public void setType(FixedValueType type) {
        this.type = type;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }



}
