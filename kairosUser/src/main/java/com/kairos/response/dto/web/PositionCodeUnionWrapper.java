package com.kairos.response.dto.web;

import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.user.country.FunctionDTO;
import com.kairos.persistence.model.user.country.ReasonCodeResponseDTO;
import com.kairos.persistence.model.user.position_code.PositionCode;

import java.util.List;

/**
 * Created by vipul on 15/2/18.
 */
public class PositionCodeUnionWrapper {
    private List<PositionCode> positionCodes;
    private List<UnionResponseDTO> unions;
    private List<OrganizationBasicResponse> organizationHierarchy;
    private List<ReasonCodeResponseDTO> reasonCodes;
    private List<FunctionDTO> functions;


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

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }

    public PositionCodeUnionWrapper(List<PositionCode> positionCodes, List<UnionResponseDTO> unions, List<OrganizationBasicResponse> organizationHierarchy, List<ReasonCodeResponseDTO> reasonCodes,List<FunctionDTO> functions) {
        this.positionCodes = positionCodes;
        this.unions = unions;
        this.organizationHierarchy = organizationHierarchy;
        this.reasonCodes = reasonCodes;
        this.functions=functions;
    }
}
