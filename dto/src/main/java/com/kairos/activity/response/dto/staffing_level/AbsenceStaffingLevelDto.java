package com.kairos.activity.response.dto.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelActivity;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by yatharth on 23/4/18.
 */
public class AbsenceStaffingLevelDto {

    BigInteger id;
    @NotNull
    private Long phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Long weekCount;
    private int minNoOfStaff;
    private int maxNoOfStaff;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public int getAvailableNoOfStaff() {
        return absentNoOfStaff;
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.absentNoOfStaff = availableNoOfStaff;

    }

    public Set<StaffingLevelActivity> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivity> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    private int absentNoOfStaff;
    private Set<StaffingLevelActivity> staffingLevelActivities=new HashSet<>();

}
