package com.kairos.planning.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TaskTimeWindow implements Comparable<DateTime>{
	private DateTime start;
	private DateTime end;
	private boolean extended;
	public TaskTimeWindow(DateTime start,DateTime end,boolean extended) {
		this.start=start;
		this.end=end;
		this.extended=extended;
	}
	@Override
	public int compareTo(DateTime o) {
		return this.start.compareTo(o);
	}
	public Interval getInterval(){
		return new Interval(start,end);
	}
	public boolean contains(DateTime time){
		return getInterval().contains(time) || getInterval().getEnd().equals(time);
	}
	public DateTime getEnd() {
		return end;
	}
	public void setEnd(DateTime end) {
		this.end = end;
	}
	public DateTime getStart() {
		return start;
	}
	public void setStart(DateTime start) {
		this.start = start;
	}
	public boolean isExtended() {
		return extended;
	}
	public void setExtended(boolean extended) {
		this.extended = extended;
	}
	public String toString(){
		return "["+start.toString("d/hh:mm")+"-"+end.toString("d/hh:mm")+"]";
	}
}
