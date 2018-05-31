package com.kairos.shiftplanning.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class SkillLineInterval implements StaffingLineInterval {
    private DateTime start;
    private DateTime end;
    private boolean required;
    private Skill skill;
    public SkillLineInterval() {
    }

    @PlanningVariable(valueRangeProviderRefs = "shifts",nullable = true)
    private ShiftRequestPhase shift;
    public SkillLineInterval(DateTime start, DateTime end, boolean required, Skill skill) {
        this.start = start;
        this.end = end;
        this.required = required;
        this.skill = skill;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    public Interval getInterval(){
        return start==null|| end==null?null:new Interval(start,end);
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ShiftRequestPhase getShift() {
        return shift;
    }

    public void setShift(ShiftRequestPhase shift) {
        this.shift = shift;
    }
    public boolean isPossibleAlongActivity(ActivityLineInterval activityLineInterval){
        return activityLineInterval.getActivityPlannerEntity().getSkills().contains(this.getSkill());
    }
}
