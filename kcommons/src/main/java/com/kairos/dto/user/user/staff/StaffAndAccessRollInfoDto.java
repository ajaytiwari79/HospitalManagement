package com.kairos.dto.user.user.staff;

import com.kairos.dto.user.access_group.UserAccessRoleDTO;

import java.util.List;

public class StaffAndAccessRollInfoDto {
    List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList;
    List<UserAccessRoleDTO> userAccessRoleDTOS;

    public StaffAndAccessRollInfoDto() {
    }

    public StaffAndAccessRollInfoDto(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList, List<UserAccessRoleDTO> userAccessRoleDTOS) {
        this.staffAdditionalInfoDTOList = staffAdditionalInfoDTOList;
        this.userAccessRoleDTOS = userAccessRoleDTOS;
    }

    public List<StaffAdditionalInfoDTO> getStaffAdditionalInfoDTOList() {
        return staffAdditionalInfoDTOList;
    }

    public void setStaffAdditionalInfoDTOList(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList) {
        this.staffAdditionalInfoDTOList = staffAdditionalInfoDTOList;
    }

    public List<UserAccessRoleDTO> getUserAccessRoleDTOS() {
        return userAccessRoleDTOS;
    }

    public void setUserAccessRoleDTOS(List<UserAccessRoleDTO> userAccessRoleDTOS) {
        this.userAccessRoleDTOS = userAccessRoleDTOS;
    }
}
