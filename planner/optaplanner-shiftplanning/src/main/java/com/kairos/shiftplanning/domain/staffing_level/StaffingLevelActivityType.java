package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.skill.Skill;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StaffingLevelActivityType {
    private String activityTypeId;
    private Set<Skill> skillSet;
    private int minimumStaffRequired;
    private int maximumStaffRequired;

}
