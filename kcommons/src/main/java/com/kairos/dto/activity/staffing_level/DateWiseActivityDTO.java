package com.kairos.dto.activity.staffing_level;/*
 *Created By Pavan on 21/8/18
 *
 */

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

public class DateWiseActivityDTO {
    private LocalDate localDate;
    private Set<BigInteger> activityIds;

    public DateWiseActivityDTO() {
        //Default Constructor
    }

    public DateWiseActivityDTO(LocalDate localDate, Set<BigInteger> activityIds) {
        this.localDate = localDate;
        this.activityIds = activityIds;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Set<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }
}
