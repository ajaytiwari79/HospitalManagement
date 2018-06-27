package com.kairos.dto.staffing_level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.staffing_level.*;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplatePeriod;
import com.kairos.enums.Day;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

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
    private List<Long> dayType=new ArrayList<>();
    private List<Day> validDays =new ArrayList<Day>();
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private boolean disabled;
    private boolean deleted ;

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
    public List<Long> getDayType() {
        return dayType;
    }
    public void setDayType(List<Long> dayType) {
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
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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



    public static StaffingLevelTemplate buildStaffingLevelTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO){
        StaffingLevelTemplate staffingLevelTemplate=new StaffingLevelTemplate();
        BeanUtils.copyProperties(staffingLevelTemplateDTO,staffingLevelTemplate);
        return staffingLevelTemplate;

    }

    public static StaffingLevelTemplate updateStaffingTemplate(BigInteger staffingTemplateId,StaffingLevelTemplateDTO staffingLevelTemplateDTO,
         StaffingLevelTemplate staffingLevelTemplate){

        BeanUtils.copyProperties(staffingLevelTemplateDTO,staffingLevelTemplate);
        staffingLevelTemplate.setId(staffingTemplateId);
        return staffingLevelTemplate;

    }

}
