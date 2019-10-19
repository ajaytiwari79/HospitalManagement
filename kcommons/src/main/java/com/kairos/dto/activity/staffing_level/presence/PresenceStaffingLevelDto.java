package com.kairos.dto.activity.staffing_level.presence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PresenceStaffingLevelDto {
    private BigInteger id;
    @NotNull
    private BigInteger phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Integer weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> presenceStaffingLevelInterval =new ArrayList<>();
    private Date updatedAt;

    public PresenceStaffingLevelDto(BigInteger phaseId, Date currentDate, Integer weekCount,
                                    StaffingLevelSetting staffingLevelSetting) {
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
    }


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date getCurrentDate() {
        return currentDate;
    }


}
