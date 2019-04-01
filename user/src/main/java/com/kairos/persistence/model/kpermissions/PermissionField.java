package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

@NodeEntity
public class PermissionField extends UserBaseEntity {

    @NotBlank(message = "error.name.notnull")
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public PermissionField() {
    }

    public PermissionField(@NotBlank(message = "error.name.notnull") String fieldName) {
        this.fieldName = fieldName;
    }
}
