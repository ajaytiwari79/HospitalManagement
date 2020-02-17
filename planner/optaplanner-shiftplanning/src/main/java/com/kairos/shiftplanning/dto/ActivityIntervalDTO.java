package com.kairos.shiftplanning.dto;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Getter
@Setter
@NoArgsConstructor
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
}
