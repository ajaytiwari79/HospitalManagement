package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

public class OrganizationAndReasonCodeDTO {
    private Long id;
    private String name;
    private List<ReasonCodeDTO> reasonCode;

    public OrganizationAndReasonCodeDTO() {
    }

    public OrganizationAndReasonCodeDTO(Long id, String name, List<ReasonCodeDTO> reasonCode) {
        this.id = id;
        this.name = name;
        this.reasonCode = reasonCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReasonCodeDTO> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(List<ReasonCodeDTO> reasonCode) {
        this.reasonCode = reasonCode;
    }
}
