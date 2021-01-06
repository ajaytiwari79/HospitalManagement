package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/**
 * Created By G.P.Ranjan on 19/10/19
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class
SkillLevelSetting {
    private int noOfStaff;
    private SkillLevel skillLevel;
    private int availableNoOfStaff;
    private Date updatedAt;

    public SkillLevelSetting(int noOfStaff, SkillLevel skillLevel, int availableNoOfStaff) {
        this.noOfStaff = noOfStaff;
        this.skillLevel = skillLevel;
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.availableNoOfStaff = availableNoOfStaff < 0 ? 0 : availableNoOfStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillLevelSetting that = (SkillLevelSetting) o;
        return skillLevel == that.skillLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillLevel);
    }
}
