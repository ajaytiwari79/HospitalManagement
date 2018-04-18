package com.planning.responseDto.staffingLevelDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigInteger;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffingLevelDTO {
    private BigInteger id;
    private Long phaseId;
    private Date currentDate;
    private Long weekCount;
    private StaffingLevelSettingDTO staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> staffingLevelInterval=new ArrayList<>();

    public StaffingLevelDTO() {
        //default constructor
    }

    public StaffingLevelDTO(Long phaseId, Date currentDate, Long weekCount,
                            StaffingLevelSettingDTO staffingLevelSetting) {
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
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

    public StaffingLevelSettingDTO getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSettingDTO staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public List<StaffingLevelTimeSlotDTO> getStaffingLevelInterval() {
        return staffingLevelInterval;
    }

    public void setStaffingLevelInterval(List<StaffingLevelTimeSlotDTO> staffingLevelInterval) {
        this.staffingLevelInterval = staffingLevelInterval;
    }

}
