package com.kairos.shiftplanning.dto;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class ActivityIntervalDTO {

    private String id;
    private ActivityLineInterval previous;
    private ActivityLineInterval next;
    private DateTime start;
    private boolean required;
    private Activity activity;
    private ShiftImp shift;
    private boolean processedForDay;
    //Duration in minutes
    private int duration;

    private int staffNo;

    public ActivityIntervalDTO(ActivityLineInterval lineInterval) {
        this.id = lineInterval.getId();
        this.previous = lineInterval.getPrevious();
        this.next = lineInterval.getNext();
        this.start = lineInterval.getStart();
        this.required = lineInterval.isRequired();
        this.activity = lineInterval.getActivity();
        this.shift = lineInterval.getShift();
        this.duration = lineInterval.getDuration();
        this.staffNo = lineInterval.getStaffNo();
    }

    public Interval getInterval(){
        return start==null?null:new Interval(start,start.plusMinutes(duration));
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(int staffNo) {
        this.staffNo = staffNo;
    }

    public boolean isProcessedForDay() {
        return processedForDay;
    }

    public void setProcessedForDay(boolean processedForDay) {
        this.processedForDay = processedForDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ActivityLineInterval getPrevious() {
        return previous;
    }

    public void setPrevious(ActivityLineInterval previous) {
        this.previous = previous;
    }

    public ActivityLineInterval getNext() {
        return next;
    }

    public void setNext(ActivityLineInterval next) {
        this.next = next;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ShiftImp getShift() {
        return shift;
    }

    public void setShift(ShiftImp shift) {
        this.shift = shift;
    }
}
