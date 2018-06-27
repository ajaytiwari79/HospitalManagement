package com.kairos.user.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@QueryResult
public class UserPermissionQueryResult {

    private Long unitId;
    private List<AccessPageQueryResult> permission = new ArrayList<>();

    public UserPermissionQueryResult(){
        // default constructor
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<AccessPageQueryResult> getPermission() {
        return permission;
    }

    public void setPermission(List<AccessPageQueryResult> permission) {
        this.permission = permission;
    }
}
