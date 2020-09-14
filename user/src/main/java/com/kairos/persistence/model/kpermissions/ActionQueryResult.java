package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.kpermissions.PermissionAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
@AllArgsConstructor
public class ActionQueryResult {
    private Long modelId;
    private Long id;
    private PermissionAction action;
    private boolean hasPermission;
}
