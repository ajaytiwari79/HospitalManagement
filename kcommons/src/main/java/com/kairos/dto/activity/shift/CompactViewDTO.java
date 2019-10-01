package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 4/10/18
 */

public class CompactViewDTO {

    private List<DetailViewDTO> staffShifts;
    private List<ReasonCodeDTO> reasonCodes;
    private Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj;

    public CompactViewDTO() {
    }

    public CompactViewDTO( List<DetailViewDTO> staffShifts, List<ReasonCodeDTO> reasonCodes,Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj) {
        this.staffShifts = staffShifts;
        this.reasonCodes = reasonCodes;
        this.assignedFunctionsObj=assignedFunctionsObj;
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
