package com.kairos.response.dto.web.staff;

import com.kairos.user.reason_code.ReasonCodeDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


public class StaffResultDTO {
    private Long staffId;
    private Long unitId;
    private String unitName;
    private String timeZone;
    private List<ReasonCodeDTO> reasonCodes;
    private Set<BigInteger> allowedTimeTypesForSick;  // added by vipul for
    public Long getStaffId() {
        return staffId;
    }

    public StaffResultDTO() {
        //dc
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

    public Set<BigInteger> getAllowedTimeTypesForSick() {
        return allowedTimeTypesForSick;
    }

    public void setAllowedTimeTypesForSick(Set<BigInteger> allowedTimeTypesForSick) {
        this.allowedTimeTypesForSick = allowedTimeTypesForSick;
    }
}
