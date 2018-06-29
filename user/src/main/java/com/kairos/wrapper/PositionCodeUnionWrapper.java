package com.kairos.wrapper;

import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.user.position_code.PositionCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 15/2/18.
 */
public class PositionCodeUnionWrapper {
    private List<PositionCode> positionCodes = new ArrayList<>();
    private List<UnionResponseDTO> unions = new ArrayList<>();
    private List<OrganizationBasicResponse> organizationHierarchy = new ArrayList<>();
    private List<ReasonCodeResponseDTO> reasonCodes = new ArrayList<>();
    private List<StaffExperienceInExpertiseDTO> staffSelectedExpertise;


    public PositionCodeUnionWrapper() {
    }

    public List<PositionCode> getPositionCodes() {
        return positionCodes;
    }

    public void setPositionCodes(List<PositionCode> positionCodes) {
        this.positionCodes = positionCodes;
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

    public PositionCodeUnionWrapper(List<PositionCode> positionCodes, List<UnionResponseDTO> unions, List<OrganizationBasicResponse> organizationHierarchy,
                                    List<ReasonCodeResponseDTO> reasonCodes, List<StaffExperienceInExpertiseDTO> staffSelectedExpertise) {
        this.positionCodes = positionCodes;
        this.unions = unions;
        this.organizationHierarchy = organizationHierarchy;
        this.reasonCodes = reasonCodes;
        this.staffSelectedExpertise = staffSelectedExpertise;
    }
}
