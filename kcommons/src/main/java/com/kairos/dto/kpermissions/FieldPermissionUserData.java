package com.kairos.dto.kpermissions;

import com.kairos.dto.user.country.agreement.cta.cta_response.AccessGroupDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FieldPermissionUserData {

    private List<AccessGroupDTO> accessGroups;
    private List<ModelDTO> modelDTOS;
    private Long currentUserStaffId;

    public FieldPermissionUserData(List<ModelDTO> modelDTOS, Long currentUserStaffId) {
        this.modelDTOS = modelDTOS;
        this.currentUserStaffId = currentUserStaffId;
    }
}
