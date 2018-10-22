package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

public class OrganizationAndReasonCodeDTO {
    private Long unitId;
    private List<ReasonCodeDTO> reasonCode;

    public OrganizationAndReasonCodeDTO() {
    }

    public OrganizationAndReasonCodeDTO(Long unitId, List<ReasonCodeDTO> reasonCode) {
        this.unitId = unitId;
        this.reasonCode = reasonCode;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<ReasonCodeDTO> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(List<ReasonCodeDTO> reasonCode) {
        this.reasonCode = reasonCode;
    }
}
