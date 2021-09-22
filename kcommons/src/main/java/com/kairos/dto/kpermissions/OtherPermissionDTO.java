package com.kairos.dto.kpermissions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtherPermissionDTO implements Serializable {

    private static final long serialVersionUID = 7618845778148781807L;
    private Set<Long> expertiseIds;
    private Set<Long> unionIds;
    private Set<Long> teamIds;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<StaffStatusEnum> staffStatuses;
    private Set<FieldLevelPermission> permissions;
    private Long staffId;

    public boolean isValid(OtherPermissionDTO otherPermissionDTO){
       boolean expertiseValid = isCollectionEmpty(expertiseIds) || CollectionUtils.containsAny(expertiseIds,otherPermissionDTO.expertiseIds);
       //TODO remove after union issue fixed
       boolean unionValid = true;//isCollectionEmpty(unionIds) || CollectionUtils.containsAny(unionIds,otherPermissionDTO.unionIds);
        boolean teamValid = isCollectionEmpty(teamIds) || CollectionUtils.containsAny(teamIds,otherPermissionDTO.teamIds);
        boolean employmentTypeValid = isCollectionEmpty(employmentTypeIds) || CollectionUtils.containsAny(employmentTypeIds,otherPermissionDTO.employmentTypeIds);
        boolean tagValid = isCollectionEmpty(tagIds) || CollectionUtils.containsAny(tagIds,otherPermissionDTO.tagIds);
        boolean staffStatusesValid = isCollectionEmpty(staffStatuses) || CollectionUtils.containsAny(staffStatuses,otherPermissionDTO.staffStatuses);
        return expertiseValid && unionValid && teamValid && employmentTypeValid && tagValid && staffStatusesValid;
    }

    @Override
    public String toString() {
        return "OtherPermissionDTO{" +
                "expertiseIds=" + expertiseIds +
                ", unionIds=" + unionIds +
                ", teamIds=" + teamIds +
                ", employmentTypeIds=" + employmentTypeIds +
                ", tagIds=" + tagIds +
                ", staffStatuses=" + staffStatuses +
                ", permissions=" + permissions +
                ", staffId=" + staffId +
                '}';
    }
}
