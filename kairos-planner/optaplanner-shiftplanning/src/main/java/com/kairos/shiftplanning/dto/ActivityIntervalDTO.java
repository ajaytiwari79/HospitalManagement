package com.kairos.shiftplanning.dto;

import com.kairos.shiftplanning.domain.ActivityPlannerEntity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class ActivityIntervalDTO {

    private String id;
    private ActivityLineInterval previous;
    private ActivityLineInterval next;
    private DateTime start;
    private boolean required;
    private ActivityPlannerEntity activityPlannerEntity;
    private ShiftRequestPhase shift;
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
        this.activityPlannerEntity = lineInterval.getActivityPlannerEntity();
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

    public ActivityPlannerEntity getActivityPlannerEntity() {
        return activityPlannerEntity;
    }

    public void setActivityPlannerEntity(ActivityPlannerEntity activityPlannerEntity) {
        this.activityPlannerEntity = activityPlannerEntity;
    }

    public ShiftRequestPhase getShift() {
        return shift;
    }

    public void setShift(ShiftRequestPhase shift) {
        this.shift = shift;
    }
}
