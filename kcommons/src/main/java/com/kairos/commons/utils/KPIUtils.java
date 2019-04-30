package com.kairos.commons.utils;

import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.Interval;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.kpi.Interval.CURRENT;
import static com.kairos.enums.kpi.Interval.LAST;
import static com.kairos.enums.kpi.Interval.NEXT;

public class KPIUtils {

    public static List<Long> getLongValue(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList()):new ArrayList<>();
    }

    public static List<LocalDate> getLocalDate(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o->DateUtils.asLocalDate((String)o)).collect(Collectors.toList()) : Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects){
        return objects.stream().map(o->new BigInteger(((Integer) o).toString())).collect(Collectors.toList());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects){
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

    public static List<DateTimeInterval> getDateTimeIntervals(Interval interval,int value,DurationType frequencyType, List<LocalDate> filterDates) {
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        //Set<DateTimeInterval> dateTimeIntervals = new TreeSet<>(Comparator.comparing(DateTimeInterval::getStartMillis));
        if(isCollectionNotEmpty(filterDates)){
            dateTimeIntervals.add(new DateTimeInterval(filterDates.get(0),filterDates.get(1)));
            return dateTimeIntervals;
        }
        LocalDate currentDate = DateUtils.getCurrentLocalDate();
        switch (interval) {
            case LAST:
                for (int i = 0; i < value; i++) {
                    currentDate = getLastDateTimeIntervalByDate(currentDate,frequencyType, dateTimeIntervals);
                }
                break;
            case CURRENT:
                getCurrentDateTimeIntervalByDate(currentDate, frequencyType, dateTimeIntervals);
                break;
            case NEXT:
                for (int i = 0; i < value; i++) {
                    currentDate = getNextDateTimeIntervalByDate(currentDate, frequencyType, dateTimeIntervals);
                }
                break;
            default:
                break;
        }
        dateTimeIntervals.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        return dateTimeIntervals;
    }

    public static LocalDate getNextDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate nextDate = getNextLocaDateByDurationType(date, durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    public static LocalDate getCurrentDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = getFirstLocalDateByDurationType(date, durationType);
        LocalDate nextDate = getLastLocaDateByDurationType(date, durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    public static LocalDate getLastDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate nextDate = getPriviousLocaDateByDurationType(date, durationType);
        dateTimeIntervals.add(new DateTimeInterval(nextDate, currentDate));
        return nextDate;
    }
}
