package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
public class StaffingLevelSkill {
    private Long skillId;
    private int noOfStaff;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .append("noOfStaff", noOfStaff)
                .toString();
    }
}
