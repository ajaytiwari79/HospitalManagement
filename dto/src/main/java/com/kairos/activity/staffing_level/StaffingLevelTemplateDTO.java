package com.kairos.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.activity.ActivityValidationError;
import com.kairos.enums.Day;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    private List<ActivityValidationError> errors;

    public StaffingLevelTemplateDTO() {
        //default constructor
    }
    public StaffingLevelTemplateDTO(String name, StaffingLevelTemplatePeriod validity) {
        this.name = name;
        this.validity = validity;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaffingLevelTemplatePeriod getValidity() {
        return validity;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public void setValidity(StaffingLevelTemplatePeriod validity) {
        this.validity = validity;
    }

    public Set<Long> getDayType() {
        return dayType;
    }

    public void setDayType(Set<Long> dayType) {
        this.dayType = dayType;
    }

    public List<Day> getValidDays() {
        return validDays;
    }

    public void setValidDays(List<Day> validDays) {
        this.validDays = validDays;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }
    public List<StaffingLevelInterval> getPresenceStaffingLevelInterval() {
        return presenceStaffingLevelInterval;
    }

    public void setPresenceStaffingLevelInterval(List<StaffingLevelInterval> presenceStaffingLevelInterval) {
        this.presenceStaffingLevelInterval = presenceStaffingLevelInterval;
    }

    public List<ActivityValidationError> getErrors() {
        return Optional.ofNullable(errors).orElse(new ArrayList<>());
    }

    public void setErrors(List<ActivityValidationError> errors) {
        this.errors = errors;
    }
}
