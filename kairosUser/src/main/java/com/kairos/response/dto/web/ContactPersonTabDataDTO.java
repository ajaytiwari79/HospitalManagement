package com.kairos.response.dto.web;

import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.organization.OrganizationServiceQueryResult;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonQueryResult;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonQueryResultByService;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonStructuredData;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.staff.StaffPersonalDetailDTO;

import java.util.List;

/**
 * Created by oodles on 4/10/17.
 */
public class ContactPersonTabDataDTO {

    List<OrganizationServiceQueryResult> organizationServices;
    List<StaffPersonalDetailDTO> staffPersonalDetailDTOS;
    List<ClientMinimumDTO> peopleHouseHolds;
    List<ClientContactPersonStructuredData> contactPersonDataList;

    public List<OrganizationServiceQueryResult> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationServiceQueryResult> organizationServices) {
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

    public List<ClientContactPersonStructuredData> getContactPersonDataList() {
        return contactPersonDataList;
    }

    public void setContactPersonDataList(List<ClientContactPersonStructuredData> contactPersonDataList) {
        this.contactPersonDataList = contactPersonDataList;
    }
}
