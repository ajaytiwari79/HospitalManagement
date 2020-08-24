package com.kairos.dto.activity.staffing_level.presence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PresenceStaffingLevelDto {
    private BigInteger id;
    private BigInteger phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Integer weekCount;
    private Set<StaffingLevelActivity> staffingLevelActivities=new HashSet<>();
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private Date updatedAt;
    private boolean draft;
    private Date startTime;
    private Date endTime;
    private StaffingLevelChange staffingLevelChange;
    private Number entityId;

    public PresenceStaffingLevelDto(BigInteger phaseId, Date currentDate, Integer weekCount,
                                    StaffingLevelSetting staffingLevelSetting) {
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public enum StaffingLevelChange{
        ACTIVITY_MIN,ACTIVITY_MAX,SKILL_BASIC,SKILL_ADVANCE,SKILL_EXPERT,REMOVE
    }


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date getCurrentDate() {
        return currentDate;
    }


}
