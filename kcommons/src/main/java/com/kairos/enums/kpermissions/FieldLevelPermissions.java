package com.kairos.enums.kpermissions;

public enum FieldLevelPermissions {
    READ("Read"),
    WRITE("Write"),
    HIDE("Hide"),
    DELETE("Delete");

    private final String value;

    FieldLevelPermissions(String value) {
        this.value = value;
    }

    public static FieldLevelPermissions getByValue(String value) {
        for (FieldLevelPermissions fieldLevelPermission : FieldLevelPermissions.values()) {
            if (fieldLevelPermission.value.equals(value)) {
                return fieldLevelPermission;
            }
        }
        return null;
    }
}