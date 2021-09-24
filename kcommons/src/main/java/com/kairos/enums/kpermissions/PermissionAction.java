package com.kairos.enums.kpermissions;

import java.util.ArrayList;
import java.util.List;

public enum PermissionAction {
    ADD("Add"),
    EDIT("Edit"),
    DELETE("Delete");
    public final String value;
    PermissionAction(String value) {
        this.value=value;
    }

    public static List<String> getValues(){
        List<String> permissionActions=new ArrayList<>();
        for (PermissionAction permissionAction:PermissionAction.values()) {
            permissionActions.add(permissionAction.value);
        }
        return permissionActions;
    }
}
