package com.kairos.shiftplanningNewVersion.entity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PlanningEntity
@EqualsAndHashCode
public class ALI implements Comparable<ALI> {

    private static Logger log = LoggerFactory.getLogger(ALI.class);

    @PlanningId
    private BigInteger id;
    private ZonedDateTime start;
    private boolean required;
    private Activity activity;
    @PlanningVariable(valueRangeProviderRefs = "shifts", nullable = true)
    private Shift shift;
    private BigInteger actualShiftId;
    private int duration;
    private int staffNo;

    public ALI(ZonedDateTime start, boolean required, int duration) {
        this.start = start;
        this.required = required;
        this.duration = duration;
    }

    @Override
    public int compareTo(ALI ali) {
        return start.compareTo(ali.getStart());
    }

    public DateTimeInterval getInterval() {
        return start == null ? null : new DateTimeInterval(start, start.plusMinutes(duration));
    }

    public ShiftActivity getShiftActivity(){
        return ShiftActivity.builder().startDate(this.start).activity(this.activity).endDate(this.getEnd()).plannedTimes(new ArrayList<>()).breakNotHeld(false).build();
    }

    public ZonedDateTime getEnd() {
        return start == null ? null : start.plusMinutes(duration);
    }

    public String getLabel() {
        return id + "---" + getInterval().toString();
    }

    @Override
    public String toString() {
        return "ALI{" +
                "start=" + start +
                ", end=" + getEnd() +
                '}';
    }
}