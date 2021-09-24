package com.kairos.dto.activity.activity.activity_tabs;/*
 *Created By Pavan on 6/10/18
 *
 */

import com.kairos.dto.user.access_permission.AccessGroupRole;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AllowedSettings {
    private Set<AccessGroupRole> canEdit;

    public AllowedSettings() {
        //Default Constructor
    }

    public Set<AccessGroupRole> getCanEdit() {
        return Optional.ofNullable(canEdit).orElse(new HashSet<>());
    }

    public void setCanEdit(Set<AccessGroupRole> canEdit) {
        this.canEdit = canEdit;
    }

}
