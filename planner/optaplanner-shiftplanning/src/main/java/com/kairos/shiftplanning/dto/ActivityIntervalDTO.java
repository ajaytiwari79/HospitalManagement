package com.kairos.shiftplanning.dto;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ActivityIntervalDTO {

    private BigInteger id;
    private ActivityLineInterval previous;
    private ActivityLineInterval next;
    private ZonedDateTime start;
    private boolean required;
    private Activity activity;
    private ShiftImp shift;
    private boolean processedForDay;
    //Duration in minutes
    private int duration;

    private int staffNo;

    public ActivityIntervalDTO(ActivityLineInterval lineInterval) {
        this.id = lineInterval.getId();
        this.start = lineInterval.getStart();
        this.required = lineInterval.isRequired();
        this.activity = lineInterval.getActivity();
        this.shift = lineInterval.getShift();
        this.duration = lineInterval.getDuration();
        this.staffNo = lineInterval.getStaffNo();
    }

    public DateTimeInterval getInterval(){
        return start==null?null:new DateTimeInterval(start,start.plusMinutes(duration));
    }
}
