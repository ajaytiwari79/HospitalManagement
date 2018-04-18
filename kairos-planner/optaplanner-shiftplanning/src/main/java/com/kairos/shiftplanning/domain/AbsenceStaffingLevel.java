package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;

import java.util.List;

@XStreamAlias("StaffingLevel")
public class AbsenceStaffingLevel {
    private String id;
    private Long unitId;
    private DateTime date;
    private List<AbsenceStaffingLevelInterval> intervals;
    private Integer intervalMinutes;
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
    public List<AbsenceStaffingLevelInterval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<AbsenceStaffingLevelInterval> intervals) {
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
}
