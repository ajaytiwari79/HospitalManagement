package com.kairos.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sachin verma
 */
public class StaffingLevelPlanningDTO {
    private BigInteger id;
    @NotNull
    private Long phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Long weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private List<StaffingLevelInterval> absenceStaffingLevelInterval =new ArrayList<>();

    public StaffingLevelPlanningDTO() {
    }

    public StaffingLevelPlanningDTO(BigInteger id, @NotNull Long phaseId, Date currentDate, Long weekCount, StaffingLevelSetting staffingLevelSetting,
                                    List<StaffingLevelInterval> presenceStaffingLevelInterval, List<StaffingLevelInterval> absenceStaffingLevelInterval) {
        this.id = id;
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
        this.presenceStaffingLevelInterval = presenceStaffingLevelInterval;
        this.absenceStaffingLevelInterval = absenceStaffingLevelInterval;
    }

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

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
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
}
