package com.kairos.activity.period;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 12/4/18.
 */
public class PeriodPhaseAndFlippingDateDTO {

    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private List<PeriodPhaseFlippingDateDTO>  phaseFlippingDate;

    public PeriodPhaseAndFlippingDateDTO(){
        // default constructor
    }

    public PeriodPhaseAndFlippingDateDTO(BigInteger currentPhaseId, BigInteger nextPhaseId, List<PeriodPhaseFlippingDateDTO> phaseFlippingDate){
        this.currentPhaseId = currentPhaseId;
        this.nextPhaseId = nextPhaseId;
        this.phaseFlippingDate = phaseFlippingDate;
    }

    public BigInteger getCurrentPhaseId() {
        return currentPhaseId;
    }

    public void setCurrentPhaseId(BigInteger currentPhaseId) {
        this.currentPhaseId = currentPhaseId;
    }

    public BigInteger getNextPhaseId() {
        return nextPhaseId;
    }

    public void setNextPhaseId(BigInteger nextPhaseId) {
        this.nextPhaseId = nextPhaseId;
    }

    public List<PeriodPhaseFlippingDateDTO> getPhaseFlippingDate() {
        return phaseFlippingDate;
    }

    public void setPhaseFlippingDate(List<PeriodPhaseFlippingDateDTO> phaseFlippingDate) {
        this.phaseFlippingDate = phaseFlippingDate;
    }
}
