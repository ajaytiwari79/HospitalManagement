package com.kairos.shiftplanning.domain.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlannedTime {
    private BigInteger plannedTimeId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
}
