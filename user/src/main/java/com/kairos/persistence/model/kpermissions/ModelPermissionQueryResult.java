package com.kairos.persistence.model.kpermissions;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.persistence.model.common.UserTranslationInfoConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
@AllArgsConstructor
public class ModelPermissionQueryResult {

    private Long id;
    private String modelName;

    private List<FieldPermissionQueryResult> fieldPermissions= new ArrayList<>();

    private List<ModelPermissionQueryResult> subModelPermissions = new ArrayList<>();

    private Set<FieldLevelPermission> permissions = new HashSet<>();
    private Set<Long> expertiseIds = new HashSet<>();
    private Set<Long> unionIds = new HashSet<>();
    private Set<Long> teamIds = new HashSet<>();
    private Set<Long> employmentTypeIds = new HashSet<>();
    private Set<Long> tagIds = new HashSet<>();
    private Set<StaffStatusEnum> staffStatuses = new HashSet<>();
    private Set<FieldLevelPermission> forOtherFieldLevelPermissions = new HashSet<>();
    private List<Map<String,Object>> actions;
    @Convert(UserTranslationInfoConverter.class)
    private Map<String, TranslationInfo> translations;
    public ModelPermissionQueryResult(Long id, String modelName) {
        this.id = id;
        this.modelName = modelName;
    }

    public ModelPermissionQueryResult(Long id, Set<FieldLevelPermission> permissions, Set<Long> expertiseIds, Set<Long> unionIds, Set<Long> teamIds, Set<Long> employmentTypeIds, Set<Long> tagIds, Set<StaffStatusEnum> staffStatuses, Set<FieldLevelPermission> forOtherFieldLevelPermissions) {
        this.id = id;
        this.permissions = permissions;
        this.expertiseIds = expertiseIds;
        this.unionIds = unionIds;
        this.teamIds = teamIds;
        this.employmentTypeIds = employmentTypeIds;
        this.tagIds = tagIds;
        this.staffStatuses = staffStatuses;
        this.forOtherFieldLevelPermissions = forOtherFieldLevelPermissions;
    }

    //Todo Don't remove it
    public OtherPermissionDTO getForOtherPermissions() {
        return new OtherPermissionDTO(expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,forOtherFieldLevelPermissions,null);
    }
}
