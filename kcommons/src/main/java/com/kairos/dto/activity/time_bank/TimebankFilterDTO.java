package com.kairos.dto.activity.time_bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimebankFilterDTO {

    private Set<BigInteger> dayTypeIds;
    private Set<BigInteger> timeSoltIds;
    private Set<DayOfWeek> dayOfWeeks;
    private Set<LocalDate> dates;
    private boolean includeDynamicCost;
    private boolean showTime;
    private boolean showActual;
    private EmploymentWithCtaDetailsDTO employment;
}
