package com.kairos.persistence.model.auth;

import java.util.Set;

/**
 * Created by prabjot on 26/9/17.
 */
public class UserPermission {

    private Long unitId;

    private Set<TabPermission> tabPermissions;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Set<TabPermission> getTabPermissions() {
        return tabPermissions;
    }

    public void setTabPermissions(Set<TabPermission> tabPermissions) {
        this.tabPermissions = tabPermissions;
    }
}
