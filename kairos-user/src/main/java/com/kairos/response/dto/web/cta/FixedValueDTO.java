package com.kairos.response.dto.web.cta;

import com.kairos.persistence.model.enums.FixedValueType;
import com.kairos.persistence.model.user.country.Currency;

public class FixedValueDTO {
    private float amount;
    private Long currencyId;
    private FixedValueType type;

    public FixedValueDTO() {
    }

    public FixedValueDTO(float amount, Long currencyId, FixedValueType type) {
        this.amount = amount;
        this.currencyId = currencyId;
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Long getCurrency() {
        return currencyId;
    }

    public void setCurrency(Currency currency) {
        this.currencyId = currencyId;
    }

    public FixedValueType getType() {
        return type;
    }

    public void setType(FixedValueType type) {
        this.type = type;
    }

    public  enum Type{
        PER_DAY,PER_ACTIVITY,PER_TASK;
    }
}
