package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 4/10/18
 */

public class CompactViewDTO {

    private Map<Long,ShiftDetailViewDTO> staffshifts;
    private List<ReasonCodeDTO> reasonCodes;

    public CompactViewDTO() {
    }

    public CompactViewDTO(Map<Long, ShiftDetailViewDTO> staffshifts, List<ReasonCodeDTO> reasonCodes) {
        this.staffshifts = staffshifts;
        this.reasonCodes = reasonCodes;
    }

    public Map<Long, ShiftDetailViewDTO> getStaffshifts() {
        return staffshifts;
    }

    public void setStaffshifts(Map<Long, ShiftDetailViewDTO> staffshifts) {
        this.staffshifts = staffshifts;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }
}
