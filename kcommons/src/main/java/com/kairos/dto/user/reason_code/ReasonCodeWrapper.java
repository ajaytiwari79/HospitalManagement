package com.kairos.dto.user.reason_code;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;

import java.util.List;

public class ReasonCodeWrapper {

    private List<ReasonCodeDTO> reasonCodes;
    private UserAccessRoleDTO userAccessRoleDTO;

    public ReasonCodeWrapper() {

    }
    public ReasonCodeWrapper(List<ReasonCodeDTO> reasonCodes,UserAccessRoleDTO userAccessRoleDTO) {
        this.reasonCodes = reasonCodes;
        this.userAccessRoleDTO = userAccessRoleDTO;
    }
    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public UserAccessRoleDTO getUserAccessRoleDTO() {
        return userAccessRoleDTO;
    }

    public void setUserAccessRoleDTO(UserAccessRoleDTO userAccessRoleDTO) {
        this.userAccessRoleDTO = userAccessRoleDTO;
    }


}
