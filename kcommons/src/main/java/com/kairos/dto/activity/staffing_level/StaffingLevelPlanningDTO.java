package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sachin verma
 */
@Getter
@Setter
public class StaffingLevelPlanningDTO {
    private BigInteger id;
    @NotNull
    private BigInteger phaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private Integer weekCount;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private List<StaffingLevelInterval> absenceStaffingLevelInterval =new ArrayList<>();

}
