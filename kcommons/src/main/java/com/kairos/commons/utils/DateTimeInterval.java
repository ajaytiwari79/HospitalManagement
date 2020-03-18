package com.kairos.commons.utils;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * @author pradeep
 * @date - 14/5/18
 */

public final class DateTimeInterval implements Comparable<DateTimeInterval>{

    private Long start;
    private Long end;


    public DateTimeInterval() {
        //default constructor please don't use for initialization
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

    /**
     *
     * @param start
     * @param end
     * @Description: This method will take start date and end date to calculate the interval
     * if end date is null then the end date will be the 2000 years after today
     */
    public DateTimeInterval(LocalDate start, LocalDate end) {
        this.start = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
         end= end==null?LocalDate.now().plusYears(2000):end;
        this.end = end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        checkInterval();
    }

    public DateTimeInterval(Long start, Long end) {
        this.start = start;
        this.end = end;
        checkInterval();
    }

    public final ZonedDateTime getStart() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault());
    }

    public final void checkInterval(){
        if(this.start>this.end){
            //throw new IllegalArgumentException("The end instant must be greater than the start instant");
        }
    }

    public final Date getStartDate() {
        return new Date(this.start);
    }

    public final Date getEndDate() {
        return new Date(this.end);
    }

    public final long getStartMillis() {
        return start;
    }

    public final void setStart(ZonedDateTime start) {
        this.start = start.toInstant().toEpochMilli();
    }

    public final void setStart(long start) {
        this.start = start;
    }

    public final ZonedDateTime getEnd() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault());
    }

    public final long getEndMillis() {
        return end;
    }

    public final void setStartFrom(LocalDateTime startFrom) {
        this.start = startFrom.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public final void setEndTo(LocalDateTime endTo) {
        this.start = endTo.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public final LocalDate getStartLocalDate() {
        return getStart().toLocalDate();
    }

    public final LocalDate getEndLocalDate() {
        return getEnd().toLocalDate();
    }


    public final LocalTime getStartLocalTime() {
        return getStart().toLocalTime();
    }

    public final LocalTime getEndLocalTime() {
        return getEnd().toLocalTime();
    }

    public final LocalDateTime getStartLocalDateTime() {
        return getStart().toLocalDateTime();
    }

    public final LocalDateTime getEndLocalDateTime() {
        return getEnd().toLocalDateTime();
    }

    public final Date getMiddleOfTheInterval(){
        return asDate(getStart().plusMinutes(this.getMinutes()/2));
    }

    public final void setEnd(ZonedDateTime end) {
        this.end = end.toInstant().toEpochMilli();
    }

    public final void setEnd(long end) {
        this.end = end;
    }

    public final DateTimeInterval overlap(DateTimeInterval interval) {
        if (!overlaps(interval)) {
            return null;
        }
        long start = Math.max(getStartMillis(), interval.getStartMillis());
        long end = Math.min(getEndMillis(), interval.getEndMillis());
        return new DateTimeInterval(start, end);
    }

    public final DateTimeInterval addInterval(DateTimeInterval interval) {
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

    public final boolean overlaps(DateTimeInterval interval) {
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

    public final DateTimeInterval gap(DateTimeInterval interval) {
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

    public final boolean contains(long millisInstant) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public final boolean contains(LocalDate localDate) {
        long millisInstant = asDate(localDate).getTime();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public final boolean contains(ZonedDateTime dateTime) {
        long millisInstant = dateTime.toInstant().toEpochMilli();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (millisInstant >= thisStart && millisInstant < thisEnd);
    }

    public final boolean contains(Date date) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() < thisEnd);
    }

    public final boolean containsAndEqualsEndDate(Date date) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() <= thisEnd);
    }

    public final boolean containsNow() {
        Date date = getDate();
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() < thisEnd);
    }

    public final boolean abuts(DateTimeInterval interval) {
        if (interval == null) {
            long now = ZonedDateTime.now().toInstant().toEpochMilli();
            return (getStartMillis() == now || getEndMillis() == now);
        } else {
            return (interval.getEndMillis() == getStartMillis() ||
                    getEndMillis() == interval.getStartMillis());
        }
    }

    public final boolean containsInterval(DateTimeInterval interval) {
        return this.start <= interval.getStartMillis() && this.end >= interval.getEndMillis();
    }

    public final long getMinutes() {
        return (this.end - this.start) / ONE_MINUTE;
    }

    public final int getHours() {
        return (int) (this.end - this.start) / 3600000;
    }

    public final int getSeconds() {
        return (int) (this.end - this.start) / 1000;
    }

    public final Long getMilliSeconds() {
        return (this.end - this.start);
    }

    public final long getDays() {
        return ChronoUnit.DAYS.between(getStart(), getEnd());
    }

    public final boolean containsStartOrEnd(Date date) {
        long thisStart = getStartMillis();
        long thisEnd = getEndMillis();
        return (date.getTime() >= thisStart && date.getTime() <= thisEnd);
    }

    public final List<DateTimeInterval> minusInterval(DateTimeInterval dateTimeInterval){
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

    public final List<DateTimeInterval> minusIntervals(List<DateTimeInterval> dateTimeIntervals){
        List<DateTimeInterval> updatedDateTimeIntervals = new ArrayList<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            updatedDateTimeIntervals.addAll(minusInterval(dateTimeInterval));
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
