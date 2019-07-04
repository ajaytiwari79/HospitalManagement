package com.kairos.dto.user.access_permission;

import java.util.*;

/**
 * Created by prerna on 21/3/18.
 */

public enum AccessGroupRole {
    STAFF("Staff"), MANAGEMENT("Management");
    private String accessGroupRole;

    AccessGroupRole() {

    }

    AccessGroupRole(String accessGroupRole) {
        this.accessGroupRole = accessGroupRole;
    }

    public static Set<AccessGroupRole> getAllRoles() {
        return new HashSet<>(EnumSet.allOf(AccessGroupRole.class));
    }

}