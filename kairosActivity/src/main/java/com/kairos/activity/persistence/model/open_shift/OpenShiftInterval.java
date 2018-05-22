package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

//Domain name can be changed
public class OpenShiftInterval extends MongoBaseEntity implements Comparable<OpenShiftInterval> {
    private int from;
    private int to;
    private BigInteger countryId;

    public OpenShiftInterval() {
        //Default Constructor
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }

    @Override
    public int compareTo(OpenShiftInterval o) {
        return this.from-o.from;
    }
}
