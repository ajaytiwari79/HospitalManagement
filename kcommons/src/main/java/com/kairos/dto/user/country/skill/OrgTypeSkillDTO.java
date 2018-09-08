package com.kairos.dto.user.country.skill;

/**
 * Created by prabjot on 12/4/17.
 */
public class OrgTypeSkillDTO {

    private Long skillId;
    private boolean isSelected;

    public Long getSkillId() {
        return skillId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }
}
