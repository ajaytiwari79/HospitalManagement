package com.kairos.response.dto.web.period;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDateDTO {
    private BigInteger phaseId;
    private Date flippingDate;

    public PeriodPhaseFlippingDateDTO(){
        // default constructor
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public Date getFlippingDate() {
        return flippingDate;
    }

    public void setFlippingDate(Date flippingDate) {
        this.flippingDate = flippingDate;
    }
}
