package com.kairos.shiftplanning.domain.shift;

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

@Getter
@Setter
@PlanningEntity
@XStreamAlias("ShiftBreak")
public class ShiftBreak {
    private String id;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartDateTimes",nullable = true)
    private DateTime startTime;
    private int order;
    private ShiftImp shift;
    private int duration;
    public ShiftBreak(){}
    public ShiftBreak(String id, int order, int duration, ShiftImp shift) {
        this.id = id;
        this.order = order;
        this.duration=duration;
        this.shift=shift;
    }

    public DateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public Interval getInterval(){
        if(startTime ==null) return null;
        return new Interval(startTime, startTime.plusMinutes(duration));
    }
    public Integer getMinutes(){
        return getInterval()==null?0:getInterval().toDuration().toStandardMinutes().getMinutes();
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
        return startTime !=null?("SB:{"+ startTime.toString("HH:mm")+"-"+getEndTime().toString("HH:mm")+"}"):"Not Planned:"+duration;
    }

    public boolean isPlannedInInterval(){
        Interval validStartInterval=ShiftPlanningUtility.getPossibleBreakStartInterval(this,this.getShift());
        //return validStartInterval.contains(startTime) || validStartInterval.getEnd().equals(startTime);
        return ShiftPlanningUtility.intervalConstainsTimeIncludingEnd(validStartInterval,startTime);
    }
}
