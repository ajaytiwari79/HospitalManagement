package com.kairos.shiftplanning.domain.unit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PlanningPeriod {

    private BigInteger id;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private Set<Long> publishEmploymentIds=new HashSet<>();
}
