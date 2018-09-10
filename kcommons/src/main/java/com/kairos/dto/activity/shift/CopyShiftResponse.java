package com.kairos.dto.activity.shift;

import java.util.ArrayList;
import java.util.List;

public class CopyShiftResponse {
    private List<StaffWiseShiftResponse> successFul = new ArrayList<>();
    private List<StaffWiseShiftResponse> failure = new ArrayList<>();
    private Integer unCopiedShiftCount;

    public CopyShiftResponse() {
        // dc
    }

    public List<StaffWiseShiftResponse> getSuccessFul() {
        return successFul;
    }

    public void setSuccessFul(List<StaffWiseShiftResponse> successFul) {
        this.successFul = successFul;
    }

    public List<StaffWiseShiftResponse> getFailure() {
        return failure;
    }

    public void setFailure(List<StaffWiseShiftResponse> failure) {
        this.failure = failure;
    }

    public Integer getUnCopiedShiftCount() {
        return unCopiedShiftCount;
    }

    public void setUnCopiedShiftCount(Integer unCopiedShiftCount) {
        this.unCopiedShiftCount = unCopiedShiftCount;
    }
}
