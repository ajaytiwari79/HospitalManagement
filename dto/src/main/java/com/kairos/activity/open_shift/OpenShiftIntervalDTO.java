package com.kairos.activity.open_shift;

import com.kairos.enums.DurationType;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;

public class OpenShiftIntervalDTO{
    private BigInteger id;
    private int from;
    private int to;
    private Long countryId;
    private DurationType type;

    public OpenShiftIntervalDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public DurationType getType() {
        return type;
    }

    public void setType(DurationType type) {
        this.type = type;
    }

    @AssertTrue(message = "from can't be less than to")
    public boolean isValid() {
        return this.to>this.from;
    }

}
