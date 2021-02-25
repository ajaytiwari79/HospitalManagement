package com.kairos.shiftplanningNewVersion.entity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.utils.LocalTimeConverter;
import com.kairos.shiftplanningNewVersion.listeners.ShiftTimeListener;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.math.BigInteger;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.asZonedDateTime;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PlanningEntity
@XStreamAlias("Shift")
public class Shift {

    @PlanningId
    private BigInteger id;
    private Staff staff;
    @CustomShadowVariable(variableListenerClass = ShiftTimeListener.class,
            sources = @PlanningVariableReference(variableName = "activityLineIntervals"))
    @XStreamConverter(LocalTimeConverter.class)
    private LocalTime startTime;
    @XStreamConverter(LocalTimeConverter.class)
    private LocalTime endTime;
    //These breaks are not useful while planner as those are realy planner entities
    @Builder.Default
    private List<ShiftBreak> breaks= new ArrayList<>();
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    @InverseRelationShadowVariable(sourceVariableName =  "shift")
    @Builder.Default
    private List<ALI> activityLineIntervals = new ArrayList<>();
    private int scheduledMinutes;
    private int durationMinutes;
    private int plannedMinutesOfTimebank;
    private boolean isLocked;
    private boolean isCreatedByStaff;
    @Builder.Default
    private List<ShiftActivity> actualShiftActivities = new ArrayList<>();
    @Builder.Default
    private List<ShiftActivity> shiftActivities = new ArrayList<>();
    @Builder.Default
    private List<ShiftActivity> breakActivities = new ArrayList<>();
    @Builder.Default
    private Set<BigInteger> activitiesTimeTypeIds = new HashSet<>();
    @Builder.Default
    private Set<BigInteger> activityIds = new HashSet<>();
    @Builder.Default
    private Set<BigInteger> activitiesPlannedTimeIds = new HashSet<>();
    private int restingMinutes;

    public DateTimeInterval getInterval(){
        return getStart()==null || getEnd()==null || getStart().isAfter(getEnd()) ? null:
                new DateTimeInterval(getStart(),getEnd());
    }

    public ZonedDateTime getStart() {
        return startTime!=null? asZonedDateTime(startDate,startTime):null;
    }

    public ZonedDateTime getEnd() {
        return endTime!=null? endTime.isAfter(startTime) ? asZonedDateTime(startDate,endTime) : asZonedDateTime(startDate.plusDays(1),endTime) :null;
    }

    public Integer getMinutes(){
        return (int)getInterval().getMinutes();
    }

    public boolean availableThisInterval(DateTimeInterval interval){
        return this.getInterval()!=null && interval!=null
                && this.getInterval().contains(interval) &&
                (getBreaks()==null || getBreaks().stream().filter(brk->brk.getInterval().overlaps(interval)).findFirst()==null);
    }

    public boolean isAbsenceActivityApplied(){
        return CollectionUtils.isNotEmpty(activityLineIntervals) && new ArrayList<>(activityLineIntervals).get(0).getActivity().isTypeAbsence();
    }
    public boolean isShiftTypeChanged() {
        return isCollectionNotEmpty(actualShiftActivities) && isCollectionNotEmpty(shiftActivities) && (isNull(actualShiftActivities.get(0).getActivity()) || !actualShiftActivities.get(0).getActivity().getTimeType().equals(shiftActivities.get(0).getActivity().getTimeType().getTimeTypeEnum()));
    }

    public boolean hasIntervalsForActivity(Activity activity){
        for(ALI ali:activityLineIntervals){
            if(ali.getActivity().getId().equals(activity.getId())){
                return true;
            }
        }
        return false;
    }

    public int missingIntervals(){
        if(isNull(this.getStart()) || isNull(this.getEnd())){
            return 0;
        }
        long totalMinutes = new DateTimeInterval(this.getStart(),this.getEnd()).getMinutes();
        for (ALI activityLineInterval : activityLineIntervals) {
            totalMinutes -= activityLineInterval.getInterval().getMinutes();
        }
        return totalMinutes <0 ? 0 : (int)totalMinutes/15;
    }
}
