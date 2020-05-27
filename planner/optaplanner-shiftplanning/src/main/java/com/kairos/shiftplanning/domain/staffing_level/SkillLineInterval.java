package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
@EqualsAndHashCode
public class SkillLineInterval implements StaffingLineInterval {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private boolean required;
    private Skill skill;

    @PlanningVariable(valueRangeProviderRefs = "shifts",nullable = true)
    private ShiftImp shift;
    public SkillLineInterval(ZonedDateTime start, ZonedDateTime end, boolean required, Skill skill) {
        this.start = start;
        this.end = end;
        this.required = required;
        this.skill = skill;
    }

    public DateTimeInterval getInterval(){
        return start==null|| end==null?null:new DateTimeInterval(start,end);
    }

    public boolean isPossibleAlongActivity(ActivityLineInterval activityLineInterval){
        return activityLineInterval.getActivity().getSkills().contains(this.getSkill());
    }
}
