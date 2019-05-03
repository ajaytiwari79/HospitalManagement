package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class KPermissionField extends UserBaseEntity {

    @NotBlank(message = "error.name.notnull")
    private String fieldName;

    @Relationship(type = HAS_PERMISSION)
    private List<AccessGroup> accessGroups = new ArrayList<>();

    public KPermissionField(@NotBlank(message = "error.name.notnull") String fieldName) {
        this.fieldName = fieldName;
    }
}
