package com.kairos.activity.staffing_level;/*
 *Created By Pavan on 21/8/18
 *
 */

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

public class DateWiseActivityDTO {
    private LocalDate date;
    private Set<BigInteger> activityIds;

    public DateWiseActivityDTO() {
        //Default Constructor
    }

    public DateWiseActivityDTO(LocalDate selectedDate, Set<BigInteger> activityIds) {
        this.date = date;
        this.activityIds = activityIds;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }
}
