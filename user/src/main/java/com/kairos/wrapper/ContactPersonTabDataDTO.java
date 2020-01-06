package com.kairos.wrapper;

import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonStructuredData;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by oodles on 4/10/17.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactPersonTabDataDTO {

    private List<OrganizationServiceQueryResult> organizationServices;
    private List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults;
    private List<ClientMinimumDTO> peopleHouseHolds;
    private List<ClientContactPersonStructuredData> contactPersonDataList;

}
