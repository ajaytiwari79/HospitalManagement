package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ShiftPlanningStaffingLevelDTO {
    private BigInteger id;
    private BigInteger phaseId;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate currentDate;
    private Integer weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelTimeSlotDTO> staffingLevelInterval=new ArrayList<>();


    public ShiftPlanningStaffingLevelDTO(BigInteger phaseId, LocalDate currentDate,Integer weekCount,
                                         StaffingLevelSetting staffingLevelSetting) {
        this.phaseId = phaseId;
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.staffingLevelSetting = staffingLevelSetting;
    }

}
