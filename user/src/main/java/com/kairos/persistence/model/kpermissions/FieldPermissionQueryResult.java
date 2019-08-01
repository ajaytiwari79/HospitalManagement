package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class FieldPermissionQueryResult {

    private Long fieldId;

    private FieldLevelPermission fieldPermission;
}