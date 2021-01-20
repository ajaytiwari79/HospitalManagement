package com.kairos.enums.kpermissions;

import java.io.Serializable;

public enum FieldLevelPermission implements Serializable {
    READ("Read"),
    WRITE("Write"),
    HIDE("Hide"),
    FOR_OTHERS("For others");
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