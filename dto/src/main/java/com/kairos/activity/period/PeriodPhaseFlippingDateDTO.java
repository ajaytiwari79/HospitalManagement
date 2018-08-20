package com.kairos.activity.period;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDateDTO {
    private BigInteger phaseId;
    private LocalDate flippingDate;
    private LocalTime flippingTime;
    public PeriodPhaseFlippingDateDTO(){
        // default constructor
    }
    public PeriodPhaseFlippingDateDTO( BigInteger phaseId, LocalDate flippingDate) {
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

    public LocalTime getFlippingTime() {
        return flippingTime;
    }

    public void setFlippingTime(LocalTime flippingTime) {
        this.flippingTime = flippingTime;
    }
}
