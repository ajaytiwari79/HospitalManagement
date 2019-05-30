package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class FieldPermissionQueryResult {

    private Long fieldId;

    private FieldLevelPermission fieldPermission;
}