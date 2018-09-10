package com.kairos.shiftplanning.utils;

import com.kairos.commons.utils.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JodaTimeUtil {

	public static boolean overlapsInStart(Interval src , Interval dest){
		return src.overlaps(dest) && !src.contains(dest.getStart()) && src.contains(dest.getEnd());
	}
	public static boolean overlapsInEnd(Interval src , Interval dest){
		return src.overlaps(dest) && src.contains(dest.getStart()) && !src.contains(dest.getEnd());
	}
	public static org.joda.time.LocalDate getJodaLocalDateFromDate(Date date) {

		LocalDate localDate = DateUtils.getLocalDateFromDate(date);
		return new org.joda.time.LocalDate(localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth());
	}
    public static org.joda.time.LocalDate getJodaLocalDateFromDate(LocalDate date) {

        return new org.joda.time.LocalDate(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
    }
	public static List<org.joda.time.LocalDate> getLocalDates(LocalDate start, LocalDate end) {
		List<org.joda.time.LocalDate> dates = new ArrayList<>();
		for(LocalDate ld = start; !ld.isAfter(end); ld=ld.plusDays(1)){
			dates.add(new org.joda.time.LocalDate(ld.getYear(),ld.getMonthValue(),ld.getYear()));
		}
		return dates;
	}

	public static List<org.joda.time.LocalDate> getLocalDates(List<LocalDate> dates) {
		List<org.joda.time.LocalDate> dateList= new ArrayList<>();
		for(LocalDate ld:dates){
			dateList.add(new org.joda.time.LocalDate(ld.getYear(),ld.getMonthValue(),ld.getYear()));
		}
		return dateList;
	}
	/**
	 * returns Joda DateTime from {@link java.util.Date} and {@link java.time.LocalTime}
	 */
	public static DateTime getDateTime(Date date, LocalTime time){
		return new DateTime(date).withMinuteOfHour(time.getMinute()).withHourOfDay(time.getHour());
	}
    public static DateTime getDateTime(LocalDate date, LocalTime time){
        return new DateTime(date.getYear(),date.getMonthValue(),date.getDayOfMonth(),time.getHour(),time.getMinute());
    }
}
