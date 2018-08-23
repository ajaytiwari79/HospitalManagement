package com.kairos.persistence.model.period;

import com.kairos.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDate {
    private BigInteger phaseId;
    private LocalDate flippingDate;
    private LocalTime flippingTime;

    public PeriodPhaseFlippingDate(){
        // default constructor
    }

    public PeriodPhaseFlippingDate(BigInteger phaseId, LocalDate flippingDate,LocalTime flippingTime){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
        this.flippingTime = flippingTime;
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

    public LocalTime getFlippingTime() {
        return flippingTime;
    }

    public void setFlippingTime(LocalTime flippingTime) {
        this.flippingTime = flippingTime;
    }
}
