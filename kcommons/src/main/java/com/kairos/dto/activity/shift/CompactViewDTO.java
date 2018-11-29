package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 4/10/18
 */

public class CompactViewDTO {

    private List<DetailViewDTO> staffShifts;
    private List<ReasonCodeDTO> reasonCodes;

    public CompactViewDTO() {
    }

    public CompactViewDTO( List<DetailViewDTO> staffShifts, List<ReasonCodeDTO> reasonCodes) {
        this.staffShifts = staffShifts;
        this.reasonCodes = reasonCodes;
    }

    public  List<DetailViewDTO> getStaffShifts() {
        return staffShifts;
    }

    public void setStaffShifts( List<DetailViewDTO> staffShifts) {
        this.staffShifts = staffShifts;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }
}
