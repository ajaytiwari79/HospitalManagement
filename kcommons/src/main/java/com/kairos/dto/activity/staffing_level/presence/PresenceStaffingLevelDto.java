package com.kairos.dto.activity.staffing_level.presence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
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

    private Date startDate;
    private Date endDate;
    private Date startTime;
    private Date endTime;
    private StaffingLevelChange staffingLevelChange;
    private StaffingLevelAction staffingLevelAction;

    public PresenceStaffingLevelDto(BigInteger phaseId, Date currentDate, Integer weekCount,
                                    StaffingLevelSetting staffingLevelSetting) {
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public enum StaffingLevelChange{
        ACTIVITY_MIN,ACTIVITY_MAX,SKILL_BASIC,SKILL_ADVANCE,SKILL_EXPERT
    }

    public enum StaffingLevelAction{
        ADD,UPDATE,REMOVE
    }


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date getCurrentDate() {
        return currentDate;
    }

    public DateTimeInterval getInterval(){
        if(this.startTime==null || this.endTime==null){
            return null;
        }
        return new DateTimeInterval(this.startTime,this.endTime);
    }


}
