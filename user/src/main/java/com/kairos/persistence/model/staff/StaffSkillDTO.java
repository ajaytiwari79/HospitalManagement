package com.kairos.persistence.model.staff;

import java.util.List;

/**
 * Created by prabjot on 3/4/17.
 */
public class StaffSkillDTO {

    private List<Long> removedSkillId;
    private boolean isSelected;
    private List<Long> assignedSkillIds;

    public List<Long> getRemovedSkillId() {
        return removedSkillId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public List<Long> getAssignedSkillIds() {
        return assignedSkillIds;
    }

    public void setRemovedSkillId(List<Long> removedSkillId) {
        this.removedSkillId = removedSkillId;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public void setAssignedSkillIds(List<Long> assignedSkillIds) {
        this.assignedSkillIds = assignedSkillIds;
    }
}
