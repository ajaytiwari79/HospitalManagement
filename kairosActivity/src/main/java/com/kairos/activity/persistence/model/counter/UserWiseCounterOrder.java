package com.kairos.activity.persistence.model.counter;

import java.math.BigInteger;

public class UserWiseCounterOrder extends UnitWiseCounterOrder {
    private BigInteger userId;

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}
