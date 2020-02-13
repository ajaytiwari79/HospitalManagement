package com.kairos.shiftplanning.domain.activity;

import com.kairos.enums.shift.ShiftStatus;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staffing_level.SkillLineInterval;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLineInterval;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Objects;

/*
 * Do not implement equals or hashcode
 */
@Getter
@Setter
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
    private BigInteger actualShiftId;




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

    public DateTime getEnd() {
        return start == null ? null : start.plusMinutes(duration);
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
