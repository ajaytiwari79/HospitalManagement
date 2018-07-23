package com.kairos.persistence.model.phase;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class ShiftAndPhaseStatusDTO {
    List<BigInteger> shiftIds;
    List<Phase.PhaseStatus> phaseStatuses;

    public ShiftAndPhaseStatusDTO() {
        //Default Constructor
    }

    public List<BigInteger> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<BigInteger> shiftIds) {
        this.shiftIds = shiftIds;
    }

    public List<Phase.PhaseStatus> getPhaseStatuses() {
        return phaseStatuses;
    }

    public void setPhaseStatuses(List<Phase.PhaseStatus> phaseStatuses) {
        this.phaseStatuses = phaseStatuses;
    }
}
