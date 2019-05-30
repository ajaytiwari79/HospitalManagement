package com.kairos.utils.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
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

public class KPIUtils {

    public static List<Long> getLongValue(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList()):new ArrayList<>();
    }

    public static List<LocalDate> getLocalDate(List<Object> objects){
        return !(ObjectUtils.isCollectionEmpty(objects))?objects.stream().map(o-> DateUtils.asLocalDate((String)o)).collect(Collectors.toList()) : Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects){
        return objects.stream().map(o->new BigInteger((o).toString())).collect(Collectors.toList());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects){
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

    public static List<DateTimeInterval> getDateTimeIntervals(IntervalUnit interval, int value, DurationType frequencyType, List<LocalDate> filterDates) {
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        if(isCollectionNotEmpty(filterDates)){
            dateTimeIntervals.add(new DateTimeInterval(filterDates.get(0),filterDates.get(1)));
            return dateTimeIntervals;
        }
        LocalDate currentDate = DateUtils.getCurrentLocalDate();
        switch (interval) {
            case LAST:
                currentDate.minusDays(1);
                for (int i = 0; i < value; i++) {
                    currentDate = getLastDateTimeIntervalByDate(currentDate,frequencyType, dateTimeIntervals);
                }
                break;
            case CURRENT:
                getCurrentDateTimeIntervalByDate(currentDate, frequencyType, dateTimeIntervals);
                break;
            case NEXT:
                currentDate=currentDate.plusDays(1);
                for (int i = 0; i < value; i++) {
                    currentDate = getNextDateTimeIntervalByDate(currentDate, frequencyType, dateTimeIntervals);
                }
                break;
            default:
                break;
        }
        Collections.sort(dateTimeIntervals);
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

    public static void sortKpiDataByDateTimeInterval(List<CommonKpiDataUnit> kpiDataUnits) {
        String label = kpiDataUnits.get(0).getLabel();
        if (label.matches("\\d{2}-\\d{2}-\\d{4}")) {
            kpiDataUnits.sort((o1, o2) -> LocalDate.parse(o1.getLabel(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).compareTo(LocalDate.parse(o2.getLabel(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        } else if (label.matches("\\d{2}-\\d{2}-\\d{4} - \\d{2}-\\d{2}-\\d{4}")) {
            kpiDataUnits.sort((o1, o2) -> LocalDate.parse(o1.getLabel().split(" ")[0].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).compareTo(LocalDate.parse(o2.getLabel().split(" ")[0].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }
    }

    public static boolean verifyKPIResponseListData(Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap){
        return  objectListMap.values().stream().flatMap(clusteredBarChartKpiDataUnits -> clusteredBarChartKpiDataUnits.stream()).anyMatch(clusteredBarChartKpiDataUnit -> !new Double(0.0).equals(clusteredBarChartKpiDataUnit.getValue()));
    }

    public static boolean verifyKPIResponseData(Map<Object, Double> objectListMap){
        return  objectListMap.values().stream().anyMatch(value -> new Double(0.0).equals(value));
    }
}
