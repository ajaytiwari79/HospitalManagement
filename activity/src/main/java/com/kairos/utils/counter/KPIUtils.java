package com.kairos.utils.counter;

import com.kairos.commons.utils.*;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.IntervalUnit;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;

public class KPIUtils {

    public static List<Long> getLongValue(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList()):new ArrayList<>();
    }

    public static List<LocalDate> getLocalDate(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o-> (o instanceof LocalDate) ? (LocalDate) o : DateUtils.asLocalDate((String)o)).collect(Collectors.toList()) : Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects){
        return objects.stream().map(o->new BigInteger((o).toString())).collect(Collectors.toList());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects){
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

    public static List<DateTimeInterval> getDateTimeIntervals(IntervalUnit interval, int value, DurationType frequencyType, List<LocalDate> filterDates,LocalDate localDate) {
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        if(isCollectionNotEmpty(filterDates)){
            dateTimeIntervals.add(new DateTimeInterval(asLocalDate(filterDates.get(0).toString()),asLocalDate(filterDates.get(1).toString())));
            return dateTimeIntervals;
        }
        if(isNull(localDate)){
            localDate = DateUtils.getCurrentLocalDate();
        }

        switch (interval) {
            case LAST:
                localDate.minusDays(1);
                for (int i = 0; i < value; i++) {
                    localDate = getLastDateTimeIntervalByDate(localDate,frequencyType, dateTimeIntervals);
                }
                break;
            case CURRENT:
                getCurrentDateTimeIntervalByDate(localDate, frequencyType, dateTimeIntervals);
                break;
            case NEXT:
                localDate=localDate.plusDays(1);
                for (int i = 0; i < value; i++) {
                    localDate = getNextDateTimeIntervalByDate(localDate, frequencyType, dateTimeIntervals);
                }
                break;
            default:
                break;
        }
        Collections.sort(dateTimeIntervals);
        return dateTimeIntervals;
    }

    public static DateTimeInterval getDateTimeInterval(IntervalUnit interval, int value, DurationType frequencyType, List<LocalDate> filterDates,LocalDate localDate) {
        DateTimeInterval dateTimeInterval = null;
        if(isCollectionNotEmpty(filterDates)){
            return new DateTimeInterval(asLocalDate(filterDates.get(0).toString()),asLocalDate(filterDates.get(1).toString()));
        }
        if(isNull(localDate)){
            localDate = DateUtils.getCurrentLocalDate();
        }
        switch (interval) {
            case LAST:
                localDate = localDate.minusDays(1);
                dateTimeInterval = new DateTimeInterval(getPriviousLocaDateByDurationType(localDate, frequencyType,1),localDate);
                break;
            case CURRENT:
                dateTimeInterval = new DateTimeInterval(getFirstLocalDateByDurationType(localDate, frequencyType),getLastLocaDateByDurationType(localDate, frequencyType));
                break;
            case NEXT:
                localDate=localDate.plusDays(1);
                dateTimeInterval = new DateTimeInterval(localDate,getNextLocaDateByDurationType(localDate, frequencyType,value));
                break;
            default:
                break;
        }
        return dateTimeInterval;
    }

    public static LocalDate getNextDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals ) {
        LocalDate currentDate = date;
        LocalDate nextDate = getNextLocaDateByDurationType(date, durationType,1);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    public static LocalDate getCurrentDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals ) {
        LocalDate currentDate = getFirstLocalDateByDurationType(date, durationType);
        LocalDate nextDate = getLastLocaDateByDurationType(date, durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    public static LocalDate getLastDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals ) {
        LocalDate currentDate = date;
        LocalDate nextDate = getPriviousLocaDateByDurationType(date, durationType,1);
        dateTimeIntervals.add(new DateTimeInterval( nextDate, currentDate));
        return nextDate;
    }

    public static void sortKpiDataByDateTimeInterval(List<CommonKpiDataUnit> kpiDataUnits) {
        if(isCollectionNotEmpty(kpiDataUnits)) {
            String label = kpiDataUnits.get(0).getLabel();
            if (label.matches("\\d{2}-\\d{2}-\\d{4}")) {
                kpiDataUnits.sort((o1, o2) -> LocalDate.parse(o1.getLabel(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).compareTo(LocalDate.parse(o2.getLabel(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            } else if (label.matches("\\d{2}-\\d{2}-\\d{4} - \\d{2}-\\d{2}-\\d{4}")) {
                kpiDataUnits.sort((o1, o2) -> LocalDate.parse(o1.getLabel().split(" ")[0].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).compareTo(LocalDate.parse(o2.getLabel().split(" ")[0].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            }
        }
    }

    public static boolean verifyKPIResponseListData(Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap){
        return  objectListMap.values().stream().flatMap(clusteredBarChartKpiDataUnits -> clusteredBarChartKpiDataUnits.stream()).anyMatch(clusteredBarChartKpiDataUnit -> !new Double(0.0).equals(clusteredBarChartKpiDataUnit.getValue()));
    }

    public static boolean verifyKPIResponseData(Map<Object, Double> objectListMap){
        return  objectListMap.values().stream().anyMatch(value -> !new Double(0.0).equals(value));
    }
}
