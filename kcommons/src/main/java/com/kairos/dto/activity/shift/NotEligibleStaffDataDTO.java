package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotEligibleStaffDataDTO {
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<Long> staffIds;
    private LocalDate shiftDate;
    private Set<BigInteger> activityIds;
    private boolean containsWTARuleViolationCriteria;
}
