package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSkill {
    private Long skillId;
    private List<SkillLevelSetting> skillLevelSettings = new ArrayList<>(3);

    public SkillLevelSetting getSkillLevelSettingBySkillLevel(SkillLevel skillLevel){
        return this.skillLevelSettings.stream().filter(skillLevelSetting -> skillLevelSetting.getSkillLevel().equals(skillLevel)).findFirst().get();
    }

    public StaffingLevelSkill(Long skillId) {
        this.skillId = skillId;
        this.skillLevelSettings=getSkillSettings();
    }

    private List<SkillLevelSetting> getSkillSettings() {
        List<SkillLevelSetting> skillLevelSettings=new ArrayList<>(3);
        skillLevelSettings.add(new SkillLevelSetting(0,SkillLevel.BASIC,0));
        skillLevelSettings.add(new SkillLevelSetting(0,SkillLevel.ADVANCE,0));
        skillLevelSettings.add(new SkillLevelSetting(0,SkillLevel.EXPERT,0));
        return skillLevelSettings;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelSkill)) return false;

        StaffingLevelSkill that = (StaffingLevelSkill) o;

        return new EqualsBuilder()
                .append(skillId, that.skillId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
}
