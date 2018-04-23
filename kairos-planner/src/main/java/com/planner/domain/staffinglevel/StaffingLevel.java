package com.planner.domain.staffinglevel;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import com.planner.domain.MongoBaseEntity;
import com.planner.domain.common.BaseEntity;
////import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class StaffingLevel extends MongoBaseEntity {
    private  BigInteger unitId;
    private Long phaseId;
    private Date currentDate;
    private Long weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> staffingLevelInterval=new ArrayList<>();
    public StaffingLevel() {
    }

    public StaffingLevel(BigInteger id, BigInteger unitId, Long phaseId, Date currentDate, Long weekCount, StaffingLevelSetting staffingLevelSetting, List<StaffingLevelTimeSlotDTO> staffingLevelInterval) {
        this.id=id;
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
        this.staffingLevelInterval = staffingLevelInterval;
        this.unitId=unitId;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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

    public List<StaffingLevelTimeSlotDTO> getStaffingLevelInterval() {
        return staffingLevelInterval;
    }

    public void setStaffingLevelInterval(List<StaffingLevelTimeSlotDTO> staffingLevelInterval) {
        this.staffingLevelInterval = staffingLevelInterval;
    }

}
