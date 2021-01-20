package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.commons.utils.DateTimeInterval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
 *Created By Pavan on 10/5/19
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlannedTime implements Serializable {
    private BigInteger plannedTimeId;
    private Date startDate;
    private Date endDate;

    @JsonIgnore
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(startDate, endDate);
    }
}
