package com.kairos.persistence.model.user.auth;

import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;

import java.util.List;
import java.util.Set;

/**
 * Created by prabjot on 26/9/17.
 */
public class UserPermission {

    private Long unitId;
    private List<AccessPageQueryResult> tabPermissions;

    public UserPermission(Long unitId, List<AccessPageQueryResult> tabPermissions) {
        this.unitId = unitId;
        this.tabPermissions = tabPermissions;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<AccessPageQueryResult> getTabPermissions() {
        return tabPermissions;
    }

    public void setTabPermissions(List<AccessPageQueryResult> tabPermissions) {
        this.tabPermissions = tabPermissions;
    }
}
