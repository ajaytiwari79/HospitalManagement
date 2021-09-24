package com.kairos.wrapper;

import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 15/2/18.
 */
public class StaffUnionWrapper {
    private List<UnionResponseDTO> unions = new ArrayList<>();
    private List<OrganizationBasicResponse> organizationHierarchy = new ArrayList<>();
    private List<ReasonCodeResponseDTO> reasonCodes = new ArrayList<>();
    private List<StaffExperienceInExpertiseDTO> staffSelectedExpertise;


    public StaffUnionWrapper() {
    }

    public List<UnionResponseDTO> getUnions() {
        return unions;
    }

    public void setUnions(List<UnionResponseDTO> unions) {
        this.unions = unions;
    }

    public List<OrganizationBasicResponse> getOrganizationHierarchy() {
        return organizationHierarchy;
    }

    public void setOrganizationHierarchy(List<OrganizationBasicResponse> organizationHierarchy) {
        this.organizationHierarchy = organizationHierarchy;
    }

    public List<ReasonCodeResponseDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeResponseDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }


    public List<StaffExperienceInExpertiseDTO> getStaffSelectedExpertise() {
        return staffSelectedExpertise;
    }

    public void setStaffSelectedExpertise(List<StaffExperienceInExpertiseDTO> staffSelectedExpertise) {
        this.staffSelectedExpertise = staffSelectedExpertise;
    }

    public StaffUnionWrapper(List<UnionResponseDTO> unions, List<OrganizationBasicResponse> organizationHierarchy,
                             List<ReasonCodeResponseDTO> reasonCodes, List<StaffExperienceInExpertiseDTO> staffSelectedExpertise) {
        this.unions = unions;
        this.organizationHierarchy = organizationHierarchy;
        this.reasonCodes = reasonCodes;
        this.staffSelectedExpertise = staffSelectedExpertise;
    }
}
