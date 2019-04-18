package com.kairos.shiftplanning.domain.shift;

import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
@XStreamAlias("ShiftBreak")
public class ShiftBreak {
    private String id;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartDateTimes",nullable = true)
    private DateTime startTime;
    private int order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShiftImp getShift() {
        return shift;
    }

    public void setShift(ShiftImp shift) {
        this.shift = shift;
    }


    private ShiftImp shift;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

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
    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    public boolean isPlannedInInterval(){
        Interval validStartInterval=ShiftPlanningUtility.getPossibleBreakStartInterval(this,this.getShift());
        //return validStartInterval.contains(startTime) || validStartInterval.getEnd().equals(startTime);
        return ShiftPlanningUtility.intervalConstainsTimeIncludingEnd(validStartInterval,startTime);
    }
}
