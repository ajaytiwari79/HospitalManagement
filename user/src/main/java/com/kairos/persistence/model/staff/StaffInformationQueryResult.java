package com.kairos.persistence.model.staff;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffInformationQueryResult {
    private Long staffId;
    private Long unitId;
    private String unitName;
    private String timeZone;
    private List<ReasonCodeDTO> reasonCodes;
    private List<EmploymentDTO> employment;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public List<EmploymentDTO> getEmployment() {
        return employment;
    }

    public void setEmployment(List<EmploymentDTO> employment) {
        this.employment = employment;
    }
}
