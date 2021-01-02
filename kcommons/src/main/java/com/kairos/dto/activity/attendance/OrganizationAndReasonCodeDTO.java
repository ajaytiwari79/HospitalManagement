package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class OrganizationAndReasonCodeDTO {
    private Long id;
    private String name;
    private List<ReasonCodeDTO> reasonCode;
    private List<EmploymentDTO> employment;

    public OrganizationAndReasonCodeDTO(Long id, String name, List<ReasonCodeDTO> reasonCode,List<EmploymentDTO> employment) {
        this.id = id;
        this.name = name;
        this.reasonCode = reasonCode;
        this.employment = employment;
    }
}
