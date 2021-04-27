package com.kairos.wrapper;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 15/2/18.
 */
@Getter
@Setter
public class StaffUnionWrapper {
    private List<UnionResponseDTO> unions = new ArrayList<>();
    private List<OrganizationBasicResponse> organizationHierarchy = new ArrayList<>();
    private List<ReasonCodeDTO> reasonCodes = new ArrayList<>();
    private List<StaffExperienceInExpertiseDTO> staffSelectedExpertise;


    public StaffUnionWrapper(List<UnionResponseDTO> unions, List<OrganizationBasicResponse> organizationHierarchy,
                             List<ReasonCodeDTO> reasonCodes, List<StaffExperienceInExpertiseDTO> staffSelectedExpertise) {
        this.unions = unions;
        this.organizationHierarchy = organizationHierarchy;
        this.reasonCodes = reasonCodes;
        this.staffSelectedExpertise = staffSelectedExpertise;
    }
}
