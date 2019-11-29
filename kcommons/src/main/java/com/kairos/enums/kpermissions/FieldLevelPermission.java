package com.kairos.enums.kpermissions;

public enum FieldLevelPermission {
    READ("Read"),
    WRITE("Write"),
    HIDE("Hide");
    /*DELETE("Delete"),
    MASK("Mask");*/

    private final String value;

    FieldLevelPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FieldLevelPermission getByValue(String value) {
        for (FieldLevelPermission fieldLevelPermission : FieldLevelPermission.values()) {
            if (fieldLevelPermission.value.equals(value)) {
                return fieldLevelPermission;
            }
        }
        return null;
    }
}