package com.kairos.dto.activity.staffing_level;/*
 *Created By Pavan on 21/8/18
 *
 */

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class DateWiseActivityDTO {
    private LocalDate localDate;
    private Set<BigInteger> activityIds;
}
