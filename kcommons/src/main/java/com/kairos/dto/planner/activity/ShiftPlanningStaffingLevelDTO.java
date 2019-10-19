package com.kairos.dto.planner.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftPlanningStaffingLevelDTO {
    private BigInteger id;
    private BigInteger phaseId;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate currentDate;
    private Integer weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval=new ArrayList<>();
}
