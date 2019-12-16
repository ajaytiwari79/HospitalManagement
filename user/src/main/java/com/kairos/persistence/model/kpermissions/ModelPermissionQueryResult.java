package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
@AllArgsConstructor
public class ModelPermissionQueryResult {

    private Long id;
    private String modelName;

    private List<FieldPermissionQueryResult> fieldPermissions= new ArrayList<>();

    private List<ModelPermissionQueryResult> subModelPermissions= new ArrayList<>();

    private Set<FieldLevelPermission> permissions;
    private Set<Long> expertiseIds;
    private Set<Long> unionIds;
    private Set<Long> teamIds;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<StaffStatusEnum> staffStatuses;
    private Set<FieldLevelPermission> forOtherFieldLevelPermissions;


}
