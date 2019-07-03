package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class ModelPermissionQueryResult {

    private Long permissionModelId;

    private List<FieldPermissionQueryResult> fieldPermissions= new ArrayList<>();

    private List<ModelPermissionQueryResult> subModelPermissions= new ArrayList<>();

    private FieldLevelPermission modelPermission;
}
