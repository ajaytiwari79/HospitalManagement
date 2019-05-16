package com.kairos.persistence.model.kpermissions;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;
@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class KPermissionFieldQueryResult {
    private KPermissionField KPermissionField;
    private KPermissionModel KPermissionModel;

}
