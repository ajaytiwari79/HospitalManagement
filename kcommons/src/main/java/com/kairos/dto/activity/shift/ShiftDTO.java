package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.attendance.AttendanceDuration;
import org.hibernate.validator.constraints.Range;
import org.joda.time.Duration;
import org.joda.time.Interval;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    private BigInteger parentOpenShiftId;
    private Long unitId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    private Long staffId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.unitPositionId.notnull")
    private Long unitPositionId;
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate shiftDate;
    private Long allowedBreakDurationInMinute;
    private ShiftTemplateDTO template;
    @NotEmpty(message = "message.shift.activity.empty")
    private List<ShiftActivity> activities = new ArrayList<>();
    private int scheduledMinutes;
    private int durationMinutes;
    private BigInteger plannedTimeId;
    private Long expertiseId;
    private LocalDate validatedByStaffDate;
    private LocalDate validatedByPlannerDate;
    private AttendanceDuration attendanceDuration;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private BigInteger shiftId;
    private boolean editable;


    public ShiftDTO(List<ShiftActivity> activities,Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
    }



    public ShiftDTO(BigInteger id, Date startDate, Date endDate, long bid, long pId, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, List<ShiftActivity> activities, Long staffId, Long unitId, Long unitPositionId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = bid;
        this.pId = pId;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
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

    public AttendanceDuration getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(AttendanceDuration attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }

    public LocalDate getValidatedByStaffDate() {
        return validatedByStaffDate;
    }

    public void setValidatedByStaffDate(LocalDate validatedByStaffDate) {
        this.validatedByStaffDate = validatedByStaffDate;
    }

    public LocalDate getValidatedByPlannerDate() {
        return validatedByPlannerDate;
    }

    public void setValidatedByPlannerDate(LocalDate validatedByPlannerDate) {
        this.validatedByPlannerDate = validatedByPlannerDate;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }


    public List<ShiftActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivity> activities) {
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



    public List<ShiftActivity> sortShifts() {
        if (Optional.ofNullable(activities).isPresent()) {
            activities.sort((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
        }
        return activities;
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

    @JsonIgnore
    public Date getActivitiesEndDate(){
        activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
        return activities.get(activities.size()-1).getEndDate();
    }

    @JsonIgnore
    public Date getActivitiesStartDate(){
        activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
        return activities.get(activities.size()-1).getEndDate();
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
                ", bonusTimeBank=" + bonusTimeBank +
                ", amount=" + amount +
                ", probability=" + probability +
                ", accumulatedTimeBankInMinutes=" + accumulatedTimeBankInMinutes +
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

    public ShiftDTO() {
        //default Const
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

}
