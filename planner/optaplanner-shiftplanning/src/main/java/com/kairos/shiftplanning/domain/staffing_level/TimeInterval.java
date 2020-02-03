package com.kairos.shiftplanning.domain.staffing_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.Interval;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeInterval {

    //In minutes
    private long startFrom;
    private long endTo;


    public boolean overlaps(TimeInterval timeInterval){
        long otherEnd = timeInterval.getEndTo();
        long otherStart = timeInterval.getStartFrom();
        //awesome
        return startFrom < otherEnd && otherStart < endTo;
    }

    public boolean overlaps(Interval interval){
        return overlaps(new TimeInterval(interval.getStart().getMinuteOfDay(),interval.getEnd().getMinuteOfDay()));
    }

    public TimeInterval overlap(Interval interval){
        return overlap(new TimeInterval(interval.getStart().getMinuteOfDay(),interval.getEnd().getMinuteOfDay()));
    }

    public boolean contains(int minutes){
        //return startFrom<minutes?minutes<endTo:false;
        return startFrom<minutes&&minutes<endTo;
    }

    public TimeInterval overlap(TimeInterval timeInterval){
        if(!this.overlaps(timeInterval)) return null;
        long start = Math.max(startFrom, timeInterval.getStartFrom());
        long end = Math.min(endTo, timeInterval.getEndTo());
        //awesome as well
        return new TimeInterval(start,end);
    }

    public long getTotalMinutes(){
        return endTo - startFrom;
    }

    public long getTotalHours(){
        return (endTo - startFrom)/60;
    }

    public long getTotalSecond(){
        return getTotalMinutes()*60;
    }

    public long getTotalMilliSec(){
        return getTotalSecond()*1000;
    }

    public TimeInterval gap(TimeInterval timeInterval){
        if(overlaps(timeInterval)) return null;
        long thisStart = this.startFrom;
        long thisEnd = this.endTo;
        long otherEnd = timeInterval.getEndTo();
        long otherStart = timeInterval.getStartFrom();
        if (thisStart > otherEnd) {
            return new TimeInterval(otherEnd, thisStart);
        } else if (otherStart > thisEnd) {
            return new TimeInterval(thisEnd, otherStart);
        } else {
            return null;
        }
    }

    public boolean abuts(TimeInterval timeInterval) {

            return (timeInterval.getEndTo() == this.startFrom ||
                    this.endTo == timeInterval.getEndTo());
    }

    @Override
    public String toString() {
        return "TimeInterval{" +
                "startFrom=" + startFrom +
                ", endTo=" + endTo +
                '}';
    }
}
