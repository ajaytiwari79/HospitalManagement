package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
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
    //private short plannedTimeInMinutes;
    private Date startDate;
    private Date endDate;

    public PlannedTime(BigInteger plannedTimeId, Date startDate, Date endDate) {
        this.plannedTimeId = plannedTimeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
