package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class FieldPermissionQueryResult {

    private Long id;
    private String fieldName;

    private Set<FieldLevelPermission> permissions;

    private Set<Long> expertiseIds = new HashSet<>();
    private Set<Long> unionIds = new HashSet<>();
    private Set<Long> teamIds = new HashSet<>();
    private Set<Long> employmentTypeIds = new HashSet<>();
    private Set<Long> tagIds = new HashSet<>();
    private Set<StaffStatusEnum> staffStatuses = new HashSet<>();
    private Set<FieldLevelPermission> forOtherFieldLevelPermissions = new HashSet<>();

    public FieldPermissionQueryResult(Long id, String fieldName) {
        this.id = id;
        this.fieldName = fieldName;
    }
}