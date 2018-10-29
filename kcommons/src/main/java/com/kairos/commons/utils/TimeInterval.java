package com.kairos.commons.utils;

import org.joda.time.Interval;

public class TimeInterval {

    //In minutes
    private long startFrom;
    private long endTo;

    public TimeInterval(long startFrom, long endTo) {
        this.startFrom = startFrom;
        this.endTo = endTo;
    }

    public long getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(long startFrom) {
        this.startFrom = startFrom;
    }

    public long getEndTo() {
        return endTo;
    }

    public void setEndTo(long endTo) {
        this.endTo = endTo;
    }

    public boolean overlaps(TimeInterval timeInterval){
        long otherEnd = timeInterval.getEndTo();
        long otherStart = timeInterval.getStartFrom();
        return startFrom < otherEnd && otherStart < endTo;
    }

    public boolean overlaps(Interval interval){
        return overlaps(new TimeInterval(interval.getStart().getMinuteOfDay(),interval.getEnd().getMinuteOfDay()));
    }

    public TimeInterval overlap(Interval interval){
        return overlap(new TimeInterval(interval.getStart().getMinuteOfDay(),interval.getEnd().getMinuteOfDay()));
    }

    public boolean contains(int minutes){
        boolean contains = startFrom<minutes?minutes<endTo:false;
        if(endTo<startFrom){
            minutes = minutes+1440;
            contains = startFrom<minutes?minutes<(endTo+1440):false;
        }
        return contains;
    }

    public TimeInterval overlap(TimeInterval timeInterval){
        if(!this.overlaps(timeInterval)) return null;
        long start = Math.max(startFrom, timeInterval.getStartFrom());
        long end = Math.min(endTo, timeInterval.getEndTo());
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
