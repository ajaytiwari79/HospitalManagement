package com.kairos.persistence.model.kpermissions;

import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;
@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class KPermissionFieldQueryResult {
    private KPermissionField kPermissionField;
    private KPermissionModel kPermissionModel;

}
