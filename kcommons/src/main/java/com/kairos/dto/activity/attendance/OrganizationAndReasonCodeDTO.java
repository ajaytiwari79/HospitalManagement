package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.EmploymentDTO;

import java.util.List;

public class OrganizationAndReasonCodeDTO {
    private Long id;
    private String name;
    private List<ReasonCodeDTO> reasonCode;
    private List<EmploymentDTO> employment;

    public OrganizationAndReasonCodeDTO() {
        //Default Constructor
    }

    public OrganizationAndReasonCodeDTO(Long id, String name, List<ReasonCodeDTO> reasonCode,List<EmploymentDTO> employment) {
        this.id = id;
        this.name = name;
        this.reasonCode = reasonCode;
        this.employment = employment;
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

    public List<EmploymentDTO> getEmployment() {
        return employment;
    }

    public void setEmployment(List<EmploymentDTO> employment) {
        this.employment = employment;
    }
}
