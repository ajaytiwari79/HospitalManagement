package com.kairos.shiftplanning.domain.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.staff.Employee;

import java.time.ZonedDateTime;
import java.util.List;

public interface Shift {
    ZonedDateTime getStart();
    ZonedDateTime getEnd();
    Employee getEmployee();
    List<ShiftBreak> getBreaks();
    void setBreaks(List<ShiftBreak> breaks);
    //boolean isRequestedByStaff();
    default DateTimeInterval getInterval(){
        return getStart()==null || getEnd()==null || getStart().isAfter(getEnd()) ? null:
                new DateTimeInterval(getStart(),getEnd());
    }
    default Integer getMinutes(){
        return getInterval()==null?0:(int)getInterval().getMinutes();
    }
    /*
    This checks whether given interval is inside shift and does not overlap a break.
     */
    default boolean availableThisInterval(DateTimeInterval interval){
        return this.getInterval()!=null && interval!=null
                && this.getInterval().contains(interval) &&
                (getBreaks()==null || getBreaks().stream().filter(brk->brk.getInterval().overlaps(interval)).findFirst()==null);
    }

    default DateTimeInterval getDateTimeInterval(){
        return getStart()==null || getEnd()==null || getStart().isAfter(getEnd()) ? null:
                new DateTimeInterval(getStart(),getEnd());
    }



}