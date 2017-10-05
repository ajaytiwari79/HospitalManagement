package com.kairos.response.dto.web;

import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonQueryResult;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.staff.StaffPersonalDetailDTO;

import java.util.List;

/**
 * Created by oodles on 4/10/17.
 */
public class ContactPersonTabDataDTO {

    List<OrganizationService> organizationServices;
    List<StaffPersonalDetailDTO> staffPersonalDetailDTOS;
    List<ClientMinimumDTO> peopleHouseHolds;
    List<ClientContactPersonQueryResult> contactPersonDataList;

    public List<OrganizationService> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationService> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<StaffPersonalDetailDTO> getStaffPersonalDetailDTOS() {
        return staffPersonalDetailDTOS;
    }

    public void setStaffPersonalDetailDTOS(List<StaffPersonalDetailDTO> staffPersonalDetailDTOS) {
        this.staffPersonalDetailDTOS = staffPersonalDetailDTOS;
    }

    public List<ClientMinimumDTO> getPeopleHouseHolds() {
        return peopleHouseHolds;
    }

    public void setPeopleHouseHolds(List<ClientMinimumDTO> peopleHouseHolds) {
        this.peopleHouseHolds = peopleHouseHolds;
    }

    public List<ClientContactPersonQueryResult> getContactPersonDataList() {
        return contactPersonDataList;
    }

    public void setContactPersonDataList(List<ClientContactPersonQueryResult> contactPersonDataList) {
        this.contactPersonDataList = contactPersonDataList;
    }
}
