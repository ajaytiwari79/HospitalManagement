package com.kairos.shiftplanning.domain.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Setter
@PlanningEntity
@XStreamAlias("ShiftBreak")
public class ShiftBreak {
    private BigInteger id;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartDateTimes",nullable = true)
    private ZonedDateTime startTime;
    private int order;
    private ShiftImp shift;
    private int duration;
    public ShiftBreak(){}
    public ShiftBreak(BigInteger id, int order, int duration, ShiftImp shift) {
        this.id = id;
        this.order = order;
        this.duration=duration;
        this.shift=shift;
    }

    public ZonedDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public DateTimeInterval getInterval(){
        if(startTime ==null) return null;
        return new DateTimeInterval(startTime, startTime.plusMinutes(duration));
    }
    public Integer getMinutes(){
        return getInterval()==null?0:(int)getInterval().getMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShiftBreak that = (ShiftBreak) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return startTime !=null?("SB:{"+ startTime.toLocalTime()+"-"+getEndTime().toLocalTime()+"}"):"Not Planned:"+duration;
    }

    public boolean isPlannedInInterval(){
        DateTimeInterval validStartInterval=ShiftPlanningUtility.getPossibleBreakStartInterval(this,this.getShift());
        //return validStartInterval.contains(startTime) || validStartInterval.getEnd().equals(startTime);
        return ShiftPlanningUtility.intervalConstainsTimeIncludingEnd(validStartInterval,startTime);
    }
}
