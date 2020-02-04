package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
public class SkillLineInterval implements StaffingLineInterval {
    private DateTime start;
    private DateTime end;
    private boolean required;
    private Skill skill;

    @PlanningVariable(valueRangeProviderRefs = "shifts",nullable = true)
    private ShiftImp shift;
    public SkillLineInterval(DateTime start, DateTime end, boolean required, Skill skill) {
        this.start = start;
        this.end = end;
        this.required = required;
        this.skill = skill;
    }

    public Interval getInterval(){
        return start==null|| end==null?null:new Interval(start,end);
    }

    public boolean isPossibleAlongActivity(ActivityLineInterval activityLineInterval){
        return activityLineInterval.getActivity().getSkills().contains(this.getSkill());
    }
}
