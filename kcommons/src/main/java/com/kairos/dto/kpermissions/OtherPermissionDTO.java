package com.kairos.dto.kpermissions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class OtherPermissionDTO {

    private Set<Long> expertiseIds;
    private Set<Long> unionIds;
    private Set<Long> teamIds;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<StaffStatusEnum> staffStatuses;
    private Set<FieldLevelPermission> forOtherFieldLevelPermissions;
}
