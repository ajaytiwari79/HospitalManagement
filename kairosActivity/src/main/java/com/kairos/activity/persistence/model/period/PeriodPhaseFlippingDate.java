package com.kairos.activity.persistence.model.period;

import com.kairos.activity.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public class PeriodPhaseFlippingDate {
    private BigInteger phaseId;
//    private Date flippingDate;
    private LocalDate flippingDate;

    public PeriodPhaseFlippingDate(){
        // default constructor
    }

    public PeriodPhaseFlippingDate(BigInteger phaseId, LocalDate flippingDate){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
    }

    /*public PeriodPhaseFlippingDate(BigInteger phaseId, Date flippingDate){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
    }*/

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

    /*public Date getFlippingDate() {
        return flippingDate;
    }

    public void setFlippingDate(Date flippingDate) {
        this.flippingDate = flippingDate;
    }*/
}
