package com.kairos.dto.user.staff.staff;

import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class UnitWiseStaffPermissionsDTO implements Serializable {

    private Boolean hub;
    private AccessGroupRole role;
    private HashMap<String, Object> hubPermissions;
    private HashMap<Long, Object> organizationPermissions;
    public List<ModelDTO> modelPermissions;
    public Long staffId;

    public Boolean isHub() {
        return hub;
    }
}
