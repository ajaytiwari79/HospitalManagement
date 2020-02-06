package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.skill.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelActivityType {
    private String activityTypeId;
    private Set<Skill> skillSet;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    public StaffingLevelActivityType( Set<Skill> skillSet,int minimumStaffRequired, int maximumStaffRequired,String activityTypeId) {
        this.minimumStaffRequired = minimumStaffRequired;
        this.skillSet = skillSet;
        this.maximumStaffRequired = maximumStaffRequired;
        this.activityTypeId=activityTypeId;
    }
}
