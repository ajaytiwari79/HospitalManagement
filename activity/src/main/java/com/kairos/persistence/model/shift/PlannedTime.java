package com.kairos.persistence.model.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

/*
 *Created By Pavan on 10/5/19
 *
 */
@Getter
@Setter
public class PlannedTime {
    private BigInteger plannedTimeId;
    private short plannedTimeInMinutes;
    private Date startDate;
    private Date endDate;
}
