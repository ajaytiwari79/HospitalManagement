package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.activity.ActivityValidationError;
import com.kairos.enums.Day;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class StaffingLevelTemplateDTO {
    private BigInteger id;
    @NotEmpty(message = "template name must not be null")
    private String name;
    private Long unitId;
    private StaffingLevelTemplatePeriod validity;
    @NotNull
    private Set<Long> dayType=new HashSet<>();
    private List<Day> validDays =new ArrayList<>();
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private boolean disabled;
    private List<ActivityValidationError> errors=new ArrayList<>();

}
