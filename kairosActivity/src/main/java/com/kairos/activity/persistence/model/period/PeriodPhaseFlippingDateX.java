package com.kairos.activity.persistence.model.period;

import com.kairos.activity.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDateX {
    private BigInteger phaseId;
    private LocalDate flippingDate;

    public PeriodPhaseFlippingDateX(){
        // default constructor
    }

    public PeriodPhaseFlippingDateX(BigInteger phaseId, LocalDate flippingDate){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public LocalDate getFlippingDate() {
        return flippingDate;
    }

    public void setFlippingDate(LocalDate flippingDate) {
        this.flippingDate = flippingDate;
    }
}
