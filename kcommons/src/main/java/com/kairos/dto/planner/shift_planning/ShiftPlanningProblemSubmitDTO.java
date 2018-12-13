package com.kairos.dto.planner.shift_planning;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 23/11/18
 */

public class ShiftPlanningProblemSubmitDTO {

    private BigInteger planningProblemId;
    private List<Long> staffIds=new ArrayList<>();
    private Long unitId;
    private BigInteger planningPeriodId;
    private LocalDate startDate;
    private LocalDate endDate;

    public ShiftPlanningProblemSubmitDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String endDate="2018/12/05";
        String startDate="2018/12/03";
        this.planningProblemId = null;
        this.staffIds.add(541l);
        //this.staffIds = staffIds;
        this.unitId = 958l;
        this.planningPeriodId = planningPeriodId;
        this.startDate =LocalDate.parse(startDate,formatter);
        this.endDate = LocalDate.parse(endDate,formatter);
    }

    public BigInteger getPlanningProblemId() {
        return planningProblemId;
    }

    public void setPlanningProblemId(BigInteger planningProblemId) {
        this.planningProblemId = planningProblemId;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getPlanningPeriodId() {
        return planningPeriodId;
    }

    public void setPlanningPeriodId(BigInteger planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
