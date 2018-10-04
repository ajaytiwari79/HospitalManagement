package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 28/9/18
 */

public class DetailViewDTO {

    private ShiftDetailViewDTO shifts;
    private List<ReasonCodeDTO> reasonCodes;


    public DetailViewDTO() {
    }

    public DetailViewDTO(ShiftDetailViewDTO shifts, List<ReasonCodeDTO> reasonCodes) {
        this.shifts = shifts;
        this.reasonCodes = reasonCodes;
    }

    public ShiftDetailViewDTO getShifts() {
        return shifts;
    }

    public void setShifts(ShiftDetailViewDTO shifts) {
        this.shifts = shifts;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }
}
