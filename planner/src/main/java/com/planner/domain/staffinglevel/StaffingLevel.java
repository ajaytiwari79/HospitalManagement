package com.planner.domain.staffinglevel;

import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.planner.domain.MongoBaseEntity;
////import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class StaffingLevel extends MongoBaseEntity {
    private  BigInteger unitId;
    private Long phaseId;
    private LocalDate date;
    private Long weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private List<StaffingLevelInterval> absenceStaffingLevelInterval =new ArrayList<>();
    public StaffingLevel() {
    }

    public StaffingLevel(BigInteger unitId, Long phaseId, LocalDate date, Long weekCount, StaffingLevelSetting staffingLevelSetting, List<StaffingLevelInterval> presenceStaffingLevelInterval,List<StaffingLevelInterval> absenceStaffingLevelInterval,BigInteger kariosId) {
        this.phaseId = phaseId;
        this.date = date;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
        this.presenceStaffingLevelInterval = presenceStaffingLevelInterval;
        this.absenceStaffingLevelInterval = absenceStaffingLevelInterval;
        this.unitId=unitId;
        this.kairosId=kariosId;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }


    public Long getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Long weekCount) {
        this.weekCount = weekCount;
    }

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }
    public BigInteger getUnitId() {
        return unitId;
    }
    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }


    public List<StaffingLevelInterval> getPresenceStaffingLevelInterval() {
        return presenceStaffingLevelInterval;
    }

    public void setPresenceStaffingLevelInterval(List<StaffingLevelInterval> presenceStaffingLevelInterval) {
        this.presenceStaffingLevelInterval = presenceStaffingLevelInterval;
    }

    public List<StaffingLevelInterval> getAbsenceStaffingLevelInterval() {
        return absenceStaffingLevelInterval;
    }

    public void setAbsenceStaffingLevelInterval(List<StaffingLevelInterval> absenceStaffingLevelInterval) {
        this.absenceStaffingLevelInterval = absenceStaffingLevelInterval;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
