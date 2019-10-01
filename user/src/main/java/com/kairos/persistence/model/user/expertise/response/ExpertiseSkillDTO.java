package com.kairos.persistence.model.user.expertise.response;

import java.util.List;

/**
 * Created by prabjot on 4/4/17.
 */
public class ExpertiseSkillDTO {

    private List<Long> skillIds;
    private boolean isSelected;

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }
}
