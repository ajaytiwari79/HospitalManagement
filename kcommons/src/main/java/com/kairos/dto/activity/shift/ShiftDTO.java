package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.shift.ShiftType;
import org.hibernate.validator.constraints.Range;
import org.joda.time.Duration;
import org.joda.time.Interval;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 30/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftDTO {

    private BigInteger id;
    private Date startDate;
    private Date endDate;
    private long bid;
    private long pId;
    private long amount;
    private long probability;
    private String remarks;
    private BigInteger parentOpenShiftId;
    private Long unitId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    private Long staffId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.unitPositionId.notnull")
    private Long unitPositionId;
    @NotNull(message = "message.shift.shiftDate")
    private LocalDate shiftDate;
    private Long allowedBreakDurationInMinute;
    private ShiftTemplateDTO template;
    @NotEmpty(message = "message.shift.activity.empty")
    private List<ShiftActivityDTO> activities = new ArrayList<>();
    private int scheduledMinutes;
    private int durationMinutes;
    private BigInteger plannedTimeId;
    private Long expertiseId;
    private LocalDate validated;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private BigInteger shiftId;
    private AccessGroupRole accessGroupRole;
    private boolean editable;
    private boolean functionDeleted;
    private ShiftType shiftType;
    private BigInteger shiftStatePhaseId;
    private int timeBankCtaBonusMinutes;
    private int deltaTimeBankMinutes;
    private long accumulatedTimeBankMinutes;
    private int plannedMinutes;
    private boolean multipleActivity;

    public ShiftDTO() {
        //default Const
    }

   public ShiftDTO(BigInteger id, Date startDate,Date endDate,Long unitId,Long staffId) {
       this.id = id;
       this.startDate = startDate;
       this.endDate = endDate;
       this.unitId = unitId;
       this.staffId = staffId;
   }

    public ShiftDTO(List<ShiftActivityDTO> activities,Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
    }





    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public int getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(int scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getClockIn() {
        return clockIn;
    }

    public void setClockIn(LocalDateTime clockIn) {
        this.clockIn = clockIn;
    }

    public LocalDateTime getClockOut() {
        return clockOut;
    }

    public void setClockOut(LocalDateTime clockOut) {
        this.clockOut = clockOut;
    }


    @JsonIgnore
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), this.getActivities().get(this.getActivities().size()-1).getEndDate().getTime());
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalDate getValidated() {
        return validated;
    }

    public void setValidated(LocalDate validated) {
        this.validated = validated;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }


    public List<ShiftActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        if (Optional.ofNullable(activities).isPresent()) {
            activities = activities.stream().filter(shiftActivityDTO -> Optional.ofNullable(shiftActivityDTO.getStartDate()).isPresent()).sorted((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate())).collect(Collectors.toList());
        }
        this.activities = activities;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public AccessGroupRole getAccessGroupRole() {
        return accessGroupRole;
    }

    public void setAccessGroupRole(AccessGroupRole accessGroupRole) {
        this.accessGroupRole = accessGroupRole;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }


    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }


    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getProbability() {
        return probability;
    }

    public void setProbability(long probability) {
        this.probability = probability;
    }

    public BigInteger getShiftStatePhaseId() {
        return shiftStatePhaseId;
    }

    public void setShiftStatePhaseId(BigInteger shiftStatePhaseId) {
        this.shiftStatePhaseId = shiftStatePhaseId;
    }

    @JsonIgnore
    public Duration getDuration() {
        return new Interval(this.activities.get(0).getStartDate().getTime(), this.activities.get(activities.size()-1).getEndDate().getTime()).toDuration();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public Date getStartDate() {
        return startDate;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }



    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /*public ShiftQueryResult getQueryResults(){
        ShiftQueryResult shiftQueryResult = new ShiftQueryResult(this.id,
                this.startDate,
                this.endDate,
                this.bid,
                this.pId,
                this.bonusTimeBank,
                this.amount,
                this.probability,
                this.accumulatedTimeBankInMinutes,
                this.remarks,
                this.activities, this.staffId, this.unitId, this.unitPositionId);
        shiftQueryResult.setStatus(this.status);
        shiftQueryResult.setAllowedBreakDurationInMinute(this.allowedBreakDurationInMinute);
        shiftQueryResult.setPlannedTimeId(this.plannedTimeId);
        return shiftQueryResult;
    }*/

    @Override
    public String toString() {
        return "ShiftDTO{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bid=" + bid +
                ", pId=" + pId +
                ", amount=" + amount +
                ", probability=" + probability +
                ", remarks='" + remarks + '\'' +
                ", unitId=" + unitId +
                ", staffId=" + staffId +
                '}';
    }


    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }

    public ShiftTemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(ShiftTemplateDTO template) {
        this.template = template;
    }

    public boolean isFunctionDeleted() {
        return functionDeleted;
    }

    public void setFunctionDeleted(boolean functionDeleted) {
        this.functionDeleted = functionDeleted;
    }

    public boolean isMultipleActivity() {
        return multipleActivity;
    }

    public void setMultipleActivity(boolean multipleActivity) {
        this.multipleActivity = multipleActivity;
    }

    public int getTimeBankCtaBonusMinutes() {
        return timeBankCtaBonusMinutes;
    }

    public void setTimeBankCtaBonusMinutes(int timeBankCtaBonusMinutes) {
        this.timeBankCtaBonusMinutes = timeBankCtaBonusMinutes;
    }

    public int getDeltaTimeBankMinutes() {
        return deltaTimeBankMinutes;
    }

    public void setDeltaTimeBankMinutes(int deltaTimeBankMinutes) {
        this.deltaTimeBankMinutes = deltaTimeBankMinutes;
    }

    public long getAccumulatedTimeBankMinutes() {
        return accumulatedTimeBankMinutes;
    }

    public void setAccumulatedTimeBankMinutes(long accumulatedTimeBankMinutes) {
        this.accumulatedTimeBankMinutes = accumulatedTimeBankMinutes;
    }

    public int getPlannedMinutes() {
        return plannedMinutes;
    }

    public void setPlannedMinutes(int plannedMinutes) {
        this.plannedMinutes = plannedMinutes;
    }
}
