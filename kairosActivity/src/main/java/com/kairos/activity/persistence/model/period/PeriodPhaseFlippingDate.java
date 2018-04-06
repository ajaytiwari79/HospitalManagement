package com.kairos.activity.persistence.model.period;

import com.kairos.activity.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDate {
    private BigInteger phaseId;
    private Date flippingDate;

    public PeriodPhaseFlippingDate(){
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
