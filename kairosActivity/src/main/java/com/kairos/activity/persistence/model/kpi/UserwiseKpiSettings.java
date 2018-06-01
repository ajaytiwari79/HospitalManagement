package com.kairos.activity.persistence.model.kpi;

import java.math.BigInteger;

public class UserwiseKpiSettings extends UnitwiseKpiSettings {
    private BigInteger userId;

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}
