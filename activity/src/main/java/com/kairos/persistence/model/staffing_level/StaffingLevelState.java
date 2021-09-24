package com.kairos.persistence.model.staffing_level;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class StaffingLevelState extends StaffingLevel {
    private BigInteger staffingLevelId;
    private BigInteger staffingLevelStatePhaseId;
    private BigInteger planningPeriodId;

    public BigInteger getStaffingLevelId() {
        return staffingLevelId;
    }

    public void setStaffingLevelId(BigInteger staffingLevelId) {
        this.staffingLevelId = staffingLevelId;
    }

    public BigInteger getStaffingLevelStatePhaseId() {
        return staffingLevelStatePhaseId;
    }

    public void setStaffingLevelStatePhaseId(BigInteger staffingLevelStatePhaseId) {
        this.staffingLevelStatePhaseId = staffingLevelStatePhaseId;
    }

    public BigInteger getPlanningPeriodId() {
        return planningPeriodId;
    }

    public void setPlanningPeriodId(BigInteger planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
    }
}
