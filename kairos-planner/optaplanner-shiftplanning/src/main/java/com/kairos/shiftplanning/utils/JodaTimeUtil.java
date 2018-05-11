package com.kairos.shiftplanning.utils;

import com.kairos.activity.util.DateUtils;
import org.joda.time.Interval;

import java.time.LocalDate;
import java.util.Date;

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
}
