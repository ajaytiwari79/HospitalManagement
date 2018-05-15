package com.kairos.shiftplanning.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

public interface Shift {
    DateTime getStart();
    DateTime getEnd();
    EmployeePlanningFact getEmployee();
    List<ShiftBreak> getBreaks();
    void setBreaks(List<ShiftBreak> breaks);
    //boolean isRequestedByStaff();
    default Interval getInterval(){
        return getStart()==null || getEnd()==null || getStart().isAfter(getEnd()) ? null:
                new Interval(getStart(),getEnd());
    }
    default Integer getMinutes(){
        return getInterval()==null?0:getInterval().toDuration().toStandardMinutes().getMinutes();
    }
    /*
    This checks whether given interval is inside shift and does not overlap a break.
     */
    default boolean availableThisInterval(Interval interval){
        return this.getInterval()!=null && interval!=null
                && this.getInterval().contains(interval) &&
                (getBreaks()==null || getBreaks().stream().filter(brk->brk.getInterval().overlaps(interval)).findFirst()==null);
    }

}