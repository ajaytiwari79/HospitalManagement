package com.kairos.user.access_permission;

/**
 * Created by prerna on 21/3/18.
 */

public enum  AccessGroupRole {
    STAFF("Staff"),MANAGEMENT("Management");
    private String accessGroupRole;
    AccessGroupRole(String accessGroupRole){
        this.accessGroupRole=accessGroupRole;
    }

}