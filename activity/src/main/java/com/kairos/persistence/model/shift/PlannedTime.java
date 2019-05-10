package com.kairos.persistence.model.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 *Created By Pavan on 10/5/19
 *
 */
@Getter
@Setter
public class PlannedTime {
    private short normalTime;
    private short overTime;
    private short extraTime;
    private Date startDate;
    private Date endDate;
}
