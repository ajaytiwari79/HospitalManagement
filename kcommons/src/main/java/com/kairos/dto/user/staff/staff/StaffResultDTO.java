package com.kairos.dto.user.staff.staff;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class StaffResultDTO {
    private Long staffId;
    private Long unitId;
    private String unitName;
    private String timeZone;
    private List<ReasonCodeDTO> reasonCodes;
    private List<EmploymentDTO> employment;
    private Set<BigInteger> allowedTimeTypesForSick;  // added by vipul for
    public Long getStaffId() {
        return staffId;
    }
}
