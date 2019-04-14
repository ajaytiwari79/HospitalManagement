package com.kairos.shiftplanning.domain.staffing_level;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

@XStreamAlias("StaffingLevelInterval")
@Deprecated
public class StaffingLevelInterval {
    private DateTime start;
    private DateTime end;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    private List<StaffingLevelSkill> skillLevels;
    private List<StaffingLevelActivityType> activityTypeLevels;

    public StaffingLevelInterval(DateTime start, DateTime end, int minimumStaffRequired, int maximumStaffRequired,
                                 List<StaffingLevelSkill> skillLevels, List<StaffingLevelActivityType> activityTypeLevels) {
        this.start = start;
        this.end = end;
        this.minimumStaffRequired = minimumStaffRequired;
        this.maximumStaffRequired = maximumStaffRequired;
        this.skillLevels = skillLevels;
        this.activityTypeLevels = activityTypeLevels;
    }

    public StaffingLevelInterval(){
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

    public List<StaffingLevelSkill> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(List<StaffingLevelSkill> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public List<StaffingLevelActivityType> getActivityTypeLevels() {
        return activityTypeLevels;
    }

    public void setActivityTypeLevels(List<StaffingLevelActivityType> activityTypeLevels) {
        this.activityTypeLevels = activityTypeLevels;
    }
    public Interval getInterval(){
        return new Interval(start,end);
    }

}
