package com.kairos.activity.staffing_level;/*
 *Created By Pavan on 21/8/18
 *
 */

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

public class DateWiseActivityDTO {
    private LocalDate selectedDate;
    private Set<BigInteger> activityIds;

    public DateWiseActivityDTO() {
        //Default Constructor
    }

    public DateWiseActivityDTO(LocalDate selectedDate, Set<BigInteger> activityIds) {
        this.selectedDate = selectedDate;
        this.activityIds = activityIds;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Set<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }
}
