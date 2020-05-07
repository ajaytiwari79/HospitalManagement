package com.kairos.dto.activity.period;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
public class PeriodDTO {
    private BigInteger id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger phaseId;
    private String currentPhaseName;
    private String nextPhaseName;
    private String phaseColor;
    private String phaseEnum;
    private Set<Long> publishEmploymentIds=new HashSet<>();
}
