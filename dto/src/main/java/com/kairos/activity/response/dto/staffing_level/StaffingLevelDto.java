package com.kairos.activity.response.dto.staffing_level;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
//import com.kairos.activity.util.validator.ExistingStaffingLevel;
//import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffingLevelDto {
    private BigInteger id;
    @NotNull
    private Long phaseId;
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //@ExistingStaffingLevel
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Long weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> staffingLevelInterval=new ArrayList<>();

    public StaffingLevelDto() {
        //default constructor
    }

    public StaffingLevelDto(Long phaseId, Date currentDate,Long weekCount,
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

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public List<StaffingLevelTimeSlotDTO> getStaffingLevelInterval() {
        return staffingLevelInterval;
    }

    public void setStaffingLevelInterval(List<StaffingLevelTimeSlotDTO> staffingLevelInterval) {
        this.staffingLevelInterval = staffingLevelInterval;
    }




}
