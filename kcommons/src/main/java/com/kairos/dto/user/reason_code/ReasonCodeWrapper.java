package com.kairos.dto.user.reason_code;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;

import java.util.List;
import java.util.Map;

public class ReasonCodeWrapper {

    private List<ReasonCodeDTO> reasonCodes;
    private UserAccessRoleDTO userAccessRoleDTO;
    private Map<String, Object> contactAddressData;

    public ReasonCodeWrapper() {

    }
    public ReasonCodeWrapper(List<ReasonCodeDTO> reasonCodes,UserAccessRoleDTO userAccessRoleDTO) {
        this.reasonCodes = reasonCodes;
        this.userAccessRoleDTO = userAccessRoleDTO;
    }

    public ReasonCodeWrapper(List<ReasonCodeDTO> reasonCodes, Map<String, Object> contactAddressData) {
        this.reasonCodes = reasonCodes;
        this.contactAddressData = contactAddressData;
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

    public Map<String, Object> getContactAddressData() {
        return contactAddressData;
    }

    public void setContactAddressData(Map<String, Object> contactAddressData) {
        this.contactAddressData = contactAddressData;
    }
}
