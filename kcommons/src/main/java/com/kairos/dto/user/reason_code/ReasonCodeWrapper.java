package com.kairos.dto.user.reason_code;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ReasonCodeWrapper {

    private List<ReasonCodeDTO> reasonCodes;
    private UserAccessRoleDTO userAccessRoleDTO;
    private Map<String, Object> contactAddressData;

    public ReasonCodeWrapper(List<ReasonCodeDTO> reasonCodes,UserAccessRoleDTO userAccessRoleDTO) {
        this.reasonCodes = reasonCodes;
        this.userAccessRoleDTO = userAccessRoleDTO;
    }

    public ReasonCodeWrapper(List<ReasonCodeDTO> reasonCodes, Map<String, Object> contactAddressData) {
        this.reasonCodes = reasonCodes;
        this.contactAddressData = contactAddressData;
    }

}
