package com.kairos.utils.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.persistence.model.counter.ApplicableKPI;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;

public class KPIUtils {

    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String DD_MMM_YY = "dd-MMM-yy";

    private KPIUtils() {
    }

    public static List<Long> getLongValue(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> ((Integer) o).longValue()).collect(Collectors.toList()) : new ArrayList<>();
    }

    public static Set<Long> getLongValueSet(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> ((Integer) o).longValue()).collect(Collectors.toSet()) : new HashSet<>();
    }

    public static List<LocalDate> getLocalDate(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> (o instanceof LocalDate) ? (LocalDate) o : DateUtils.asLocalDate((String) o)).collect(Collectors.toList()) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects) {
        return objects.stream().map(o -> new BigInteger((o).toString())).collect(Collectors.toList());
    }
    public static Set<BigInteger> getBigIntegerSet(List<Object> objects) {
        return objects.stream().map(o -> new BigInteger((o).toString())).collect(Collectors.toSet());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects) {
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

    public static <T> Set<String> getStringByList(Set<T> objects) {
        return objects.stream().map(o -> o.toString()).collect(Collectors.toSet());
    }



    public static List<DateTimeInterval> getDateTimeIntervals(IntervalUnit interval, int value, DurationType frequencyType, List<LocalDate> filterDates, LocalDate localDate) {
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        if (isCollectionNotEmpty(filterDates)) {
            return getDateTimeIntervalsByHours(frequencyType, filterDates, dateTimeIntervals);
        }
        if (isNull(localDate)) {
            localDate = DateUtils.getCurrentLocalDate();
        }
        switch (interval) {
            case LAST:
                localDate = getLastDateByFrequencyType(frequencyType,localDate);
                for (int i = 0; i < value; i++) {
                    localDate = getLastDateTimeIntervalByDate(localDate, frequencyType, dateTimeIntervals);
                }
                break;
            case CURRENT:
                getCurrentDateTimeIntervalByDate(localDate, frequencyType, dateTimeIntervals);
                break;
            case NEXT:
                localDate = getNextDateByFrequencyType(frequencyType,localDate);
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



    private static List<DateTimeInterval> getDateTimeIntervalsByHours(DurationType frequencyType, List<LocalDate> filterDates, List<DateTimeInterval> dateTimeIntervals) {
        if(DurationType.HOURS.equals(frequencyType)){
            dateTimeIntervals= filterDates.get(0).equals(filterDates.get(1)) ?getDateTimeIntervalByDates(filterDates.get(0),filterDates.get(1)):getDateIntervalByDates(filterDates.get(0),filterDates.get(1));
        }else{
            dateTimeIntervals.add(new DateTimeInterval(asLocalDate(filterDates.get(0).toString()), asLocalDate(filterDates.get(1).toString())));
        }
        return dateTimeIntervals;
    }

    public static  List<DateTimeInterval>  getDateTimeIntervalByDates(LocalDate startDate,LocalDate endDate) {
        List<DateTimeInterval> dateTimeIntervals=new ArrayList<>();
        LocalDateTime startOfTheDay=startDate.atStartOfDay();
        LocalDateTime endOfTheDay=getEndOfDayFromLocalDate(endDate);
        while (!startOfTheDay.isAfter(endOfTheDay)) {
            dateTimeIntervals.add(new DateTimeInterval(asDate(startOfTheDay), asDate(startOfTheDay.plusHours(1))));
           startOfTheDay=startOfTheDay.plusHours(1);
        }
        return dateTimeIntervals;
    }

    public static  List<DateTimeInterval>  getDateIntervalByDates(LocalDate startDate,LocalDate endDate) {
        List<DateTimeInterval> dateTimeIntervals=new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            dateTimeIntervals.add(new DateTimeInterval(asDate(startDate), asDate(startDate.plusDays(1))));
            startDate=startDate.plusDays(1);
        }
        return dateTimeIntervals;
    }

    public static LocalDate getNextDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate nextDate = getNextLocaDateByDurationType(date, durationType);
        dateTimeIntervals.add(new DateTimeInterval(asDate(currentDate), getEndOfDayDateFromLocalDate(nextDate)));
        return nextDate.plusDays(1);
    }

    public static void getCurrentDateTimeIntervalByDate(LocalDate localDate, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate firstLocalDate = getFirstLocalDateByDurationType(localDate,durationType);
        Date date = asDate(firstLocalDate);
        dateTimeIntervals.add(new DateTimeInterval(date, getEndOfDayDateFromLocalDate(getNextLocaDateByDurationType(firstLocalDate, durationType))));
    }

    public static LocalDate getLastDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate lastDate = getPriviousLocaDateByDurationType(date, durationType, 1);
        dateTimeIntervals.add(new DateTimeInterval(asDate(lastDate), getEndOfDayDateFromLocalDate(currentDate)));
        return lastDate.minusDays(1);
    }

    public static void sortKpiDataByDateTimeInterval(List<CommonKpiDataUnit> kpiDataUnits) {
        if (isCollectionNotEmpty(kpiDataUnits)) {
            String label = kpiDataUnits.get(0).getLabel();
            if (label.matches("\\d{2}-\\D{3}-\\d{2}")) {
                kpiDataUnits.sort(Comparator.comparing(o -> LocalDate.parse(o.getLabel(), DateTimeFormatter.ofPattern(DD_MMM_YY))));
            } else if (label.matches("\\d{2}-\\D{3}-\\d{2} - \\d{2}-\\D{3}-\\d{2}")) {
                kpiDataUnits.sort(Comparator.comparing(o -> LocalDate.parse(o.getLabel().split(" ")[0].trim(), DateTimeFormatter.ofPattern(DD_MMM_YY))));
            }
        }
    }

    public static boolean verifyKPIResponseListData(Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap) {
        return objectListMap.values().stream().flatMap(Collection::stream).anyMatch(clusteredBarChartKpiDataUnit -> !
                Double.valueOf(0.0).equals(clusteredBarChartKpiDataUnit.getValue()));
    }

    public static boolean verifyKPIResponseData(Map<Object, Double> objectListMap) {
        return objectListMap.values().stream().anyMatch(value -> !Double.valueOf(0.0).equals(value));
    }

    public static Double getValueWithDecimalFormat(Double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.valueOf(decimalFormat.format(value));
    }

    public static void getKpiDataUnits(Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        for (Map.Entry<Object, List<ClusteredBarChartKpiDataUnit>> entry : objectListMap.entrySet()) {
            if (REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation())) {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue()));
            } else {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue()));
            }

        }
    }
}
