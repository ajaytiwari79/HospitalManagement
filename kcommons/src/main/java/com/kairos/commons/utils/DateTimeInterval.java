package com.kairos.commons.utils;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * @author pradeep
 * @date - 14/5/18
 */

public class DateTimeInterval implements Comparable<DateTimeInterval>{

    private Long start;
    private Long end;


    public DateTimeInterval() {
        //default constructor
    }

    public DateTimeInterval(ZonedDateTime start, ZonedDateTime end) {
        this.start = start.toInstant().toEpochMilli();
        this.end = end.toInstant().toEpochMilli();
        checkInterval();
    }

    public DateTimeInterval(Date start, Date end) {
        this.start = start.getTime();
        this.end = end.getTime();
        checkInterval();
    }

    public DateTimeInterval(LocalDate start, LocalDate end) {
        this.start = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.end = end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        checkInterval();
    }

    public DateTimeInterval(Long start, Long end) {
        this.start = start;
        this.end = end;
        checkInterval();
    }

    public ZonedDateTime getStart() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault());
    }

    public void checkInterval(){
        if(this.start>this.end){
            //throw new IllegalArgumentException("The end instant must be greater than the start instant");
        }
    }

    public Date getStartDate() {
        return new Date(this.start);
    }

    public Date getEndDate() {
        return new Date(this.end);
    }

    public long getStartMillis() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start.toInstant().toEpochMilli();
    }

    public void setStart(long start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault());
    }

    public long getEndMillis() {
        return end;
    }

    public void setStartFrom(LocalDateTime startFrom) {
        this.start = startFrom.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public void setEndTo(LocalDateTime endTo) {
        this.start = endTo.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public LocalDate getStartLocalDate() {
        return getStart().toLocalDate();
    }

    public LocalDate getEndLocalDate() {
        return getEnd().toLocalDate();
    }


    public LocalTime getStartLocalTime() {
        return getStart().toLocalTime();
    }

    public LocalTime getEndLocalTime() {
        return getEnd().toLocalTime();
    }

    public LocalDateTime getStartLocalDateTime() {
        return getStart().toLocalDateTime();
    }

    public LocalDateTime getEndLocalDateTime() {
        return getEnd().toLocalDateTime();
    }

    public Date getMiddleOfTheInterval(){
        return asDate(getStart().plusMinutes(this.getMinutes()/2));
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end.toInstant().toEpochMilli();
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public DateTimeInterval overlap(DateTimeInterval interval) {
        if (!overlaps(interval)) {
            return null;
        }
        long start = Math.max(getStartMillis(), interval.getStartMillis());
        long end = Math.min(getEndMillis(), interval.getEndMillis());
        return new DateTimeInterval(start, end);
    }

    public DateTimeInterval addInterval(DateTimeInterval interval) {
        DateTimeInterval dateTimeInterval = null;
        if (isNull(interval)) {
            dateTimeInterval = this;
        } else {
            long start = Math.min(this.start, interval.getStartMillis());
            long end = Math.max(this.end, interval.getEndMillis());
            dateTimeInterval = new DateTimeInterval(start, end);
        }
        return dateTimeInterval;
    }

    public boolean overlaps(DateTimeInterval interval) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        if (interval == null) {
            long now = ZonedDateTime.now().toInstant().toEpochMilli();
            return (thisStart < now && now < thisEnd);
        } else {
            long otherStart = interval.getStartMillis();
            long otherEnd = interval.getEndMillis();
            return (thisStart < otherEnd && otherStart < thisEnd);
        }
    }

    public DateTimeInterval gap(DateTimeInterval interval) {
        long otherStart = interval.getStartMillis();
        long otherEnd = interval.getEndMillis();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        if (thisStart > otherEnd) {
            return new DateTimeInterval(otherEnd, thisStart);
        } else if (otherStart > thisEnd) {
            return new DateTimeInterval(thisEnd, otherStart);
        } else {
            return null;
        }
    }

    public boolean contains(long millisInstant) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public boolean contains(LocalDate localDate) {
        long millisInstant = asDate(localDate).getTime();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public boolean contains(ZonedDateTime dateTime) {
        long millisInstant = dateTime.toInstant().toEpochMilli();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public boolean contains(Date date) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() < thisEnd);
    }

    public boolean containsNow() {
        Date date = new Date();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() < thisEnd);
    }

    public boolean abuts(DateTimeInterval interval) {
        if (interval == null) {
            long now = ZonedDateTime.now().toInstant().toEpochMilli();
            return (getStartMillis() == now || getEndMillis() == now);
        } else {
            return (interval.getEndMillis() == getStartMillis() ||
                    getEndMillis() == interval.getStartMillis());
        }
    }

    public boolean containsInterval(DateTimeInterval interval) {
        return this.start <= interval.getStartMillis() && this.end >= interval.getEndMillis();
    }

    public long getMinutes() {
        return (this.end - this.start) / ONE_MINUTE;
    }

    public int getHours() {
        return (int) (this.end - this.start) / 3600000;
    }

    public int getSeconds() {
        return (int) (this.end - this.start) / 1000;
    }

    public Long getMilliSeconds() {
        return (this.end - this.start);
    }

    public long getDays() {
        return ChronoUnit.DAYS.between(getStart(), getEnd());
    }

    public boolean containsStartOrEnd(Date date) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() <= thisEnd);
    }

    public List<DateTimeInterval> minusInterval(DateTimeInterval dateTimeInterval){
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        if(this.overlaps(dateTimeInterval)){
            if(this.start<dateTimeInterval.start && this.end>dateTimeInterval.end){
                dateTimeIntervals.add(new DateTimeInterval(this.start,dateTimeInterval.start));
                dateTimeIntervals.add(new DateTimeInterval(dateTimeInterval.end,this.end));
            }else if(this.contains(dateTimeInterval.start) && dateTimeInterval.contains(this.end)){
                dateTimeIntervals.add(new DateTimeInterval(this.start,dateTimeInterval.start));
            }else if (this.contains(dateTimeInterval.end) && dateTimeInterval.contains(this.start)){
                dateTimeIntervals.add(new DateTimeInterval(dateTimeInterval.end,this.end));
            }
        }
        return dateTimeIntervals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DateTimeInterval interval = (DateTimeInterval) o;

        return new EqualsBuilder()
                .append(start, interval.start)
                .append(end, interval.end)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(start)
                .append(end)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "DateTimeInterval{" +
                "start=" + getStart() +
                ", end=" + getEnd() +
                '}';
    }

    @Override
    public int compareTo(DateTimeInterval dateTimeInterval) {
        return this.start.compareTo(dateTimeInterval.start);
    }
}
