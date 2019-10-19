package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSkill {
    private Long skillId;
    private int noOfStaff;
    private SkillLevel skillLevel;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .append("noOfStaff", noOfStaff)
                .append("skillLevel", skillLevel.value)
                .toString();
    }
}
