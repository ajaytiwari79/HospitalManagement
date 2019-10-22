package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSkill {
    private Long skillId;
    private Set<SkillLevelSetting> skillLevelSettings = new HashSet<>(3);

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .toString();
    }
}
