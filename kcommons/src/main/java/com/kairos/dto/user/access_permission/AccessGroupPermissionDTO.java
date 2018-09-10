package com.kairos.dto.user.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by prabjot on 30/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessGroupPermissionDTO {

    private List<Long> accessPageIds;
    private boolean isSelected;

    public List<Long> getAccessPageIds() {
        return accessPageIds;
    }

    public void setAccessPageIds(List<Long> accessPageIds) {
        this.accessPageIds = accessPageIds;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
