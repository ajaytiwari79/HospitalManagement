package com.kairos.persistence.model.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "staffing_level")
public class StaffingLevel extends MongoBaseEntity {
    @Indexed
    private Date currentDate;
    private Integer weekCount;
    @Indexed
    private Long unitId;
    private BigInteger phaseId;
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();

    public List<StaffingLevelInterval> getAbsenceStaffingLevelInterval() {
        return absenceStaffingLevelInterval;
    }

    public void setAbsenceStaffingLevelInterval(List<StaffingLevelInterval> absenceStaffingLevelInterval) {
        this.absenceStaffingLevelInterval = absenceStaffingLevelInterval;
    }

    private List<StaffingLevelInterval> absenceStaffingLevelInterval =new ArrayList<>();;



    public StaffingLevel() {
        //default constructor
    }


    public StaffingLevel(Date currentDate, Integer weekCount,
                         Long organizationId, BigInteger phaseId, StaffingLevelSetting staffingLevelSetting) {
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.unitId = organizationId;
        this.phaseId = phaseId;
        this.staffingLevelSetting = staffingLevelSetting;
    }
    public StaffingLevel(Date currentDate, int weekCount,
                         Long organizationId, BigInteger phaseId) {
        this.currentDate = currentDate;
        this.weekCount = weekCount;
        this.unitId = organizationId;
        this.phaseId = phaseId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Integer weekCount) {
        this.weekCount = weekCount;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
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

        if (!(o instanceof StaffingLevel)) return false;

        StaffingLevel that = (StaffingLevel) o;

        return new EqualsBuilder()
                .append(currentDate, that.currentDate)
                .append(weekCount, that.weekCount)
                .append(unitId, that.unitId)
                .append(phaseId, that.phaseId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(currentDate)
                .append(weekCount)
                .append(unitId)
                .append(phaseId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("currentDate", currentDate)
                .append("weekCount", weekCount)
                .append("unitId", unitId)
                .append("phaseId", phaseId)
                .append("staffingLevelSetting", staffingLevelSetting)
                .append("presenceStaffingLevelInterval", presenceStaffingLevelInterval)
                .toString();
    }
}
