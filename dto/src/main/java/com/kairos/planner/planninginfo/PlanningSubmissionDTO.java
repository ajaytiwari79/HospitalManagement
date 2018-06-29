package com.kairos.planner.planninginfo;

import com.kairos.util.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class PlanningSubmissionDTO {
    private List<LocalDate> dates;
    private Long unitId;
    private BigInteger solverConfigId;

    public PlanningSubmissionDTO() {
    }
    private PlanningSubmissionDTO(Long unitId,BigInteger solverConfigId){
        this.unitId=unitId;
        this.solverConfigId=solverConfigId;

    }
    public PlanningSubmissionDTO(LocalDate start,LocalDate end, Long unitId,BigInteger solverConfigId) {
        this(unitId,solverConfigId);
        this.dates = DateUtils.getDates(start,end);
    }

    public PlanningSubmissionDTO(List<LocalDate> dates, Long unitId,BigInteger solverConfigId) {
        this(unitId,solverConfigId);
        this.dates = dates;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
    }
}
