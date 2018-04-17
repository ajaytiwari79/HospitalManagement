package com.kairos.shiftplanning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

@XStreamAlias("StaffingLevelInterval")
public class AbsenceStaffingLevelInterval {
    private DateTime start;
    private DateTime end;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    private List<StaffingLevelSkill> skillLevel;
    private List<StaffingLevelActivityType> activityTypeLevel;
    public AbsenceStaffingLevelInterval(DateTime start, DateTime end, int minimumStaffRequired, int maximumStaffRequired,
                                        List<StaffingLevelSkill> skillLevel, List<StaffingLevelActivityType> activityTypeLevel) {
        this.start = start;
        this.end = end;
        this.minimumStaffRequired = minimumStaffRequired;
        this.maximumStaffRequired = maximumStaffRequired;
        this.skillLevel=skillLevel;
        this.activityTypeLevel=activityTypeLevel;
    }
    public AbsenceStaffingLevelInterval(){
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public int getMinimumStaffRequired() {
        return minimumStaffRequired;
    }

    public void setMinimumStaffRequired(int minimumStaffRequired) {
        this.minimumStaffRequired = minimumStaffRequired;
    }

    public int getMaximumStaffRequired() {
        return maximumStaffRequired;
    }

    public void setMaximumStaffRequired(int maximumStaffRequired) {
        this.maximumStaffRequired = maximumStaffRequired;
    }

    public List<StaffingLevelSkill> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(List<StaffingLevelSkill> skillLevel) {
        this.skillLevel = skillLevel;
    }

    public List<StaffingLevelActivityType> getActivityTypeLevel() {
        return activityTypeLevel;
    }

    public void setActivityTypeLevel(List<StaffingLevelActivityType> activityTypeLevel) {
        this.activityTypeLevel = activityTypeLevel;
    }
    public Interval getInterval(){
        return new Interval(start,end);
    }

}
