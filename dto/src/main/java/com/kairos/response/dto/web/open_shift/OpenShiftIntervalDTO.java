package com.kairos.response.dto.web.open_shift;

import java.math.BigInteger;

public class OpenShiftIntervalDTO {
    private BigInteger id;
    private int from;
    private int to;

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
}
