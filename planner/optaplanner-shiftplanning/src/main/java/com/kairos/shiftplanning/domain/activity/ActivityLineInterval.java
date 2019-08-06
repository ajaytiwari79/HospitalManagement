package com.kairos.shiftplanning.domain.activity;

import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staffing_level.SkillLineInterval;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLineInterval;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/*
 * Do not implement equals or hashcode
 */
@PlanningEntity
public class ActivityLineInterval implements StaffingLineInterval, Comparable<ActivityLineInterval> {

    private static Logger log = LoggerFactory.getLogger(ActivityLineInterval.class);

    private String id;
    private ActivityLineInterval previous;
    private ActivityLineInterval next;
    private DateTime start;
    private boolean required;
    private Activity activity;
    @PlanningVariable(valueRangeProviderRefs = "shifts", nullable = true)
    private ShiftImp shift;

    public ActivityLineInterval() {
    }

    private int duration;
    private int staffNo;

    public ActivityLineInterval(String id, DateTime start, int duration, boolean required, Activity activity, int staffNo) {
        this.id = id;
        this.start = start;
        this.duration = duration;
        this.required = required;
        this.activity = activity;
        this.staffNo = staffNo;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

    public Interval getInterval() {
        return start == null ? null : new Interval(start, start.plusMinutes(duration));
    }

    public String getIntervalAsString() {
        return ShiftPlanningUtility.getIntervalAsString(getInterval());
    }

    @Deprecated
    public boolean overlaps(StaffingLineInterval staffingLineInterval) {
        return Objects.equals(this.getInterval(), staffingLineInterval.getInterval()) &&
                this.getClass().isInstance(staffingLineInterval) || this.getActivity().getSkills().contains(((SkillLineInterval) staffingLineInterval).getSkill());
    }

    public ShiftImp getShift() {
        return shift;
    }

    public void setShift(ShiftImp shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return id + "-" + staffNo + "-" + activity.getName() + "-" + getIntervalAsString();
    }

    public String getLabel() {
        return id + "---" + getIntervalAsString();
    }

    public boolean similarInterval(Interval interval) {
        return this.getInterval().equals(interval);
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DateTime getEnd() {
        return start == null ? null : start.plusMinutes(duration);
    }

    public int getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(int staffNo) {
        this.staffNo = staffNo;
    }

    public String getId() {
        return id;
    }

    public boolean overlapsActivity(ActivityLineInterval ali) {
        boolean overlaps = staffNo != ali.staffNo && start.equals(ali.start) && Objects.equals(shift, ali.shift);
        return overlaps;
    }

    //TODO this should be a shift constraint for performance of fetching a shift is easier than fethcing a ali from drools memory
    public boolean overlapOnBreak() {
        return shift.getBreaks() != null && shift.getBreaks().stream().filter(brk -> brk.getInterval().overlaps(this.getInterval())).findFirst().isPresent();
    }

    @Override
    public int compareTo(ActivityLineInterval o) {
        return this.getStart().compareTo(o.getStart());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ActivityLineInterval that = (ActivityLineInterval) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
