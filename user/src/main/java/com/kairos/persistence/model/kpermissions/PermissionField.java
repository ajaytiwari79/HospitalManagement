package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@NodeEntity
public class PermissionField extends UserBaseEntity {

    @NotBlank(message = "error.name.notnull")
    private String fieldName;

    @Relationship(type = HAS_PERMISSION)
    private List<AccessGroup> accessGroups = new ArrayList<>();

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public PermissionField() {
    }

    public List<AccessGroup> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroup> accessGroups) {
        this.accessGroups = accessGroups;
    }

    public PermissionField(@NotBlank(message = "error.name.notnull") String fieldName) {
        this.fieldName = fieldName;
    }
}
