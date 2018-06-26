package com.kairos.activity.persistence.model.counter;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class UserWiseCounterOrder extends UnitWiseCounterOrder {
    private BigInteger userId;

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}
