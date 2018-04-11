package com.kairos.persistence.model.organization;

/**
 * Created by prabjot on 12/4/17.
 */
public class OrgTypeExpertiseDTO {

    private Long expertiseId;
    private boolean isSelected;

    public Long getExpertiseId() {
        return expertiseId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }
}
