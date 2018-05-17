package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
@XStreamAlias("staffinglevel")
public class StaffingLevel {
    private String id;
    private Long unitId;
    private LocalDate date;
    private List<StaffingLevelInterval> intervals;
    private Integer intervalMinutes;
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public List<StaffingLevelInterval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<StaffingLevelInterval> intervals) {
        this.intervals = intervals;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }
    @Deprecated
    public Integer getStaffingLevelSatisfaction(List<ShiftConstrutionPhase> shifts){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shifts);
    }
    public Integer getStaffingLevelSatisfaction(Shift shift,List<IndirectActivity> indirectActivityList){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shift,indirectActivityList);
    }
    public Integer getStaffingLevelSatisfaction(List<Shift> shifts,List<IndirectActivity> indirectActivityList){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shifts,indirectActivityList);
    }
}
