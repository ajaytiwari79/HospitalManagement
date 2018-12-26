package com.kairos.dto.activity.staffing_level.presence;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.kairos.util.validator.ExistingStaffingLevel;
//import org.springframework.format.annotation.DateTimeFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PresenceStaffingLevelDto {
    private BigInteger id;
    @NotNull
    private BigInteger phaseId;
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //@ExistingStaffingLevel
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Integer weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> presenceStaffingLevelInterval =new ArrayList<>();
    private Date updatedAt;

    public PresenceStaffingLevelDto() {
        //default constructor
    }

    public PresenceStaffingLevelDto(BigInteger phaseId, Date currentDate, Integer weekCount,
                                    StaffingLevelSetting staffingLevelSetting) {
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

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
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

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public List<StaffingLevelTimeSlotDTO> getPresenceStaffingLevelInterval() {
        return presenceStaffingLevelInterval;
    }

    public void setPresenceStaffingLevelInterval(List<StaffingLevelTimeSlotDTO> presenceStaffingLevelInterval) {
        this.presenceStaffingLevelInterval = presenceStaffingLevelInterval;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
