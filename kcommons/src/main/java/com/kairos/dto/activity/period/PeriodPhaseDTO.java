package com.kairos.dto.activity.period;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by prerna on 6/4/18.
 */
@Getter
@Setter
public class PeriodPhaseDTO {
    private BigInteger phaseId;
    private LocalDate flippingDate;
    private LocalTime flippingTime;
}
