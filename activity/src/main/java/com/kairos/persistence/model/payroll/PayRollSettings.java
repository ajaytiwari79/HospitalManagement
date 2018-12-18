package com.kairos.persistence.model.payroll;
/*
 *Created By Pavan on 18/12/18
 *
 */

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class PayRollSettings extends MongoBaseEntity {
    private BigInteger payRollId;
    private Long countryId;
    private boolean byDefault;

    public PayRollSettings() {
        //Default Constructor
    }

    public BigInteger getPayRollId() {
        return payRollId;
    }

    public void setPayRollId(BigInteger payRollId) {
        this.payRollId = payRollId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isByDefault() {
        return byDefault;
    }

    public void setByDefault(boolean byDefault) {
        this.byDefault = byDefault;
    }
}
