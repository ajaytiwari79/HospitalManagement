package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.commons.utils.DateUtils;
import org.hibernate.validator.constraints.Range;
import org.joda.time.Duration;
import org.joda.time.Interval;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by vipul on 30/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftDTO {
    private BigInteger id;
    private String name;
    private Date startDate;
    private Date endDate;
    private long bid;
    private long pId;
    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    private BigInteger parentOpenShiftId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.activityId.notnull")
    private BigInteger activityId;
    private Long unitId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    private Long staffId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.unitPositionId.notnull")
    private Long unitPositionId;
    private int scheduledMinutes;
    private int durationMinutes;
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate shiftDate;
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate startLocalDate;
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate endLocalDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private Long allowedBreakDurationInMinute;
    private List<ShiftDTO> subShifts = new ArrayList<>();
    private BigInteger templateId;
    private String timeType;
    private Set<ShiftStatus> status = new HashSet<>();
    private BigInteger plannedTimeId;

    public ShiftDTO(@Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId) {
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
    }

    public ShiftDTO(@Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId,LocalDate startLocalDate,LocalDate endLocalDate,LocalTime startTime,LocalTime endTime) {
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
        this.startLocalDate=startLocalDate;
        this.endLocalDate=endLocalDate;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public ShiftDTO(@Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull")
            Long unitPositionId,LocalDate startLocalDate,LocalDate endLocalDate,LocalTime startTime,LocalTime endTime,List<ShiftDTO> subShifts) {
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
        this.startLocalDate=startLocalDate;
        this.endLocalDate=endLocalDate;
        this.startTime=startTime;
        this.endTime=endTime;
        this.subShifts=subShifts;
    }

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public void setStartLocalDate(LocalDate startLocalDate) {
        this.startLocalDate = startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public void setEndLocalDate(LocalDate endLocalDate) {
        this.endLocalDate = endLocalDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
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

    public long getBonusTimeBank() {
        return bonusTimeBank;
    }

    public void setBonusTimeBank(long bonusTimeBank) {
        this.bonusTimeBank = bonusTimeBank;
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

    public long getAccumulatedTimeBankInMinutes() {
        return accumulatedTimeBankInMinutes;
    }

    public void setAccumulatedTimeBankInMinutes(long accumulatedTimeBankInMinutes) {
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
    }

    public Duration getDuration() {
        return new Interval(this.getStartDate().getTime(), this.getEndDate().getTime()).toDuration();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
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
        return startLocalDate != null && startTime != null ? DateUtils.getDateByLocalDateAndLocalTime(startLocalDate, startTime) : null;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return startLocalDate != null && startTime != null ? DateUtils.getDateByLocalDateAndLocalTime(endLocalDate, endTime) : null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ShiftQueryResult getQueryResults(){
        ShiftQueryResult shiftQueryResult = new ShiftQueryResult(this.id, this.name,
                this.startDate,
                this.endDate,
                this.bid,
                this.pId,
                this.bonusTimeBank,
                this.amount,
                this.probability,
                this.accumulatedTimeBankInMinutes,
                this.remarks,
                this.activityId, this.staffId, this.unitId, this.unitPositionId);
        shiftQueryResult.setDurationMinutes(this.getDurationMinutes());
        shiftQueryResult.setScheduledMinutes(this.getScheduledMinutes());
        shiftQueryResult.setStatus(this.status);
        shiftQueryResult.setAllowedBreakDurationInMinute(this.allowedBreakDurationInMinute);
        shiftQueryResult.setPlannedTimeId(this.plannedTimeId);
        return shiftQueryResult;
    }

    @Override
    public String toString() {
        return "ShiftDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bid=" + bid +
                ", pId=" + pId +
                ", bonusTimeBank=" + bonusTimeBank +
                ", amount=" + amount +
                ", probability=" + probability +
                ", accumulatedTimeBankInMinutes=" + accumulatedTimeBankInMinutes +
                ", remarks='" + remarks + '\'' +
                ", activityId=" + activityId +
                ", unitId=" + unitId +
                ", staffId=" + staffId +
                '}';
    }

    public List<ShiftDTO> getSubShifts() {
        return subShifts;
    }

    public void setSubShifts(List<ShiftDTO> subShifts) {
        this.subShifts = subShifts;
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

    public ShiftDTO() {
        //default Const
    }




    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public void setTemplateId(BigInteger templateId) {
        this.templateId = templateId;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
}
