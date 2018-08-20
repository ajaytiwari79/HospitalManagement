package com.kairos.persistence.model.period;

import com.kairos.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDate {
    private BigInteger phaseId;
    private LocalDateTime flippingDate;

    public PeriodPhaseFlippingDate(){
        // default constructor
    }

    public PeriodPhaseFlippingDate(BigInteger phaseId, LocalDateTime flippingDate){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public LocalDateTime getFlippingDate() {
        return flippingDate;
    }

    public void setFlippingDate(LocalDateTime flippingDate) {
        this.flippingDate = flippingDate;
    }

}
