package com.kairos.shiftplanning.utils;

import org.joda.time.Interval;

public class JodaTimeUtil {

	public static boolean overlapsInStart(Interval src , Interval dest){
		return src.overlaps(dest) && !src.contains(dest.getStart()) && src.contains(dest.getEnd());
	}
	public static boolean overlapsInEnd(Interval src , Interval dest){
		return src.overlaps(dest) && src.contains(dest.getStart()) && !src.contains(dest.getEnd());
	}
}
