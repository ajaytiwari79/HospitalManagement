package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.commons.utils.DateTimeInterval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Date;

/*
 *Created By Pavan on 10/5/19
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PlannedTime {
    private BigInteger plannedTimeId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public PlannedTime(BigInteger plannedTimeId, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.plannedTimeId = plannedTimeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @JsonIgnore
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(startDate,endDate);
    }
}
