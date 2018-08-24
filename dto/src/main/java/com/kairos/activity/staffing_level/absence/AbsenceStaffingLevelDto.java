package com.kairos.activity.staffing_level.absence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.activity.staffing_level.StaffingLevelActivity;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yatharth on 23/4/18.
 */
public class AbsenceStaffingLevelDto {

    BigInteger id;
    @NotNull
    private BigInteger phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Integer weekCount;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int absentNoOfStaff;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    private Long unitId;

    public AbsenceStaffingLevelDto() {

    }

    public AbsenceStaffingLevelDto(BigInteger id, BigInteger phaseId, Date currentDate, Integer weekCount) {

        this.id = id;
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Integer weekCount) {
        this.weekCount = weekCount;
    }

    public int getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public void setMinNoOfStaff(int minNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
    }

    public int getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public void setMaxNoOfStaff(int maxNoOfStaff) {
        this.maxNoOfStaff = maxNoOfStaff;
    }



    public Set<StaffingLevelActivity> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivity> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public int getAbsentNoOfStaff() {
        return absentNoOfStaff;
    }

    public void setAbsentNoOfStaff(int absentNoOfStaff) {
        this.absentNoOfStaff = absentNoOfStaff;
    }

    private Set<StaffingLevelActivity> staffingLevelActivities=new HashSet<>();

}
