package com.kairos.persistence.model.period;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by prerna on 6/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class PeriodPhaseFlippingDate {
    private BigInteger phaseId;
    private BigInteger schedulerPanelId;
    private LocalDate flippingDate;
    private LocalTime flippingTime;

    public PeriodPhaseFlippingDate(BigInteger phaseId, LocalDate flippingDate,LocalTime flippingTime){
        this.phaseId = phaseId;
        this.flippingDate = flippingDate;
        this.flippingTime = flippingTime;
    }

}
