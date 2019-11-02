package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSkill {
    private Long skillId;
    private List<SkillLevelSetting> skillLevelSettings = new ArrayList<>(3);

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .toString();
    }
}
