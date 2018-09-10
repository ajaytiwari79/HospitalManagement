package com.kairos.persistence.model.staffing_level;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.StaffingLevelTemplatePeriod;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.Day;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "staffing_level_template")
public class StaffingLevelTemplate extends MongoBaseEntity{
    private String name;
    private Long unitId;
    private StaffingLevelTemplatePeriod validity;
    private Set<Long> dayType=new HashSet<>();
    private List<Day> validDays =new ArrayList<>();
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private boolean disabled;

    public StaffingLevelTemplate() {
        //default constructor
    }

    public StaffingLevelTemplate(String name, List<Day> validDayTypes,
                                 StaffingLevelTemplatePeriod validity, StaffingLevelSetting staffingLevelSetting) {
        this.name=name;
        this.validDays = validDayTypes;
        this.validity = validity;
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public StaffingLevelTemplatePeriod getValidity() {
        return validity;
    }

    public void setValidity(StaffingLevelTemplatePeriod validity) {
        this.validity = validity;
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
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void addStaffingLevelTimeSlot(StaffingLevelInterval staffingLevelTimeSlot) {
        if (staffingLevelTimeSlot == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getPresenceStaffingLevelInterval().add(staffingLevelTimeSlot);

    }

    public void addStaffingLevelTimeSlot(Set<StaffingLevelInterval> staffingLevelTimeSlots) {
        if (staffingLevelTimeSlots == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getPresenceStaffingLevelInterval().addAll(staffingLevelTimeSlots);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelTemplate)) return false;

        StaffingLevelTemplate that = (StaffingLevelTemplate) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(validity, that.validity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(validity)
                .toHashCode();
    }
}
