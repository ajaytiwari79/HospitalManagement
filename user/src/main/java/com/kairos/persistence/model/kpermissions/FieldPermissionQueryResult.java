package com.kairos.persistence.model.kpermissions;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class FieldPermissionQueryResult {

    private Long id;
    private String fieldName;

    private Set<FieldLevelPermission> permissions = new HashSet<>();

    private Set<Long> expertiseIds = new HashSet<>();
    private Set<Long> unionIds = new HashSet<>();
    private Set<Long> teamIds = new HashSet<>();
    private Set<Long> employmentTypeIds = new HashSet<>();
    private Set<Long> tagIds = new HashSet<>();
    private Set<StaffStatusEnum> staffStatuses = new HashSet<>();
    private Set<FieldLevelPermission> forOtherFieldLevelPermissions = new HashSet<>();
    private Map<String, TranslationInfo> translations;

    public FieldPermissionQueryResult(Long id, String fieldName) {
        this.id = id;
        this.fieldName = fieldName;
    }

    //Todo Don't remove it
    public OtherPermissionDTO getForOtherPermissions() {
        return new OtherPermissionDTO(expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,forOtherFieldLevelPermissions,null);
    }

    public FieldPermissionQueryResult(Long id, Set<FieldLevelPermission> permissions, Set<Long> expertiseIds, Set<Long> unionIds, Set<Long> teamIds, Set<Long> employmentTypeIds, Set<Long> tagIds, Set<StaffStatusEnum> staffStatuses, Set<FieldLevelPermission> forOtherFieldLevelPermissions,String fieldName) {
        this.id = id;
        this.permissions = permissions;
        this.expertiseIds = expertiseIds;
        this.unionIds = unionIds;
        this.teamIds = teamIds;
        this.employmentTypeIds = employmentTypeIds;
        this.tagIds = tagIds;
        this.staffStatuses = staffStatuses;
        this.forOtherFieldLevelPermissions = forOtherFieldLevelPermissions;
        this.fieldName = fieldName;
    }
}