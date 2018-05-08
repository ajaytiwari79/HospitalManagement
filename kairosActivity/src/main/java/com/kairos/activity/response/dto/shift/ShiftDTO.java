package com.kairos.activity.response.dto.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.util.DateUtils;
import com.kairos.enums.shift.ShiftState;
import org.hibernate.validator.constraints.Range;
import org.joda.time.Duration;
import org.joda.time.Interval;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @JsonFormat(pattern = "HH:MM")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:MM")
    private LocalTime endTime;




    public ShiftDTO(@Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId) {
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
    }

    public ShiftDTO(String name, Date startDate, Date endDate, @Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.unitPositionId.notnull") Long unitPositionId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
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

    private List<ShiftDTO> subShifts = new ArrayList<>();

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

    public Duration getDuration(){
        return new Interval(this.getStartDate().getTime(),this.getEndDate().getTime()).toDuration();
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
        return startLocalDate!=null && startTime!=null?DateUtils.getDateByLocalDateAndLocalTime(startLocalDate,startTime):null;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return startLocalDate!=null && startTime!=null?DateUtils.getDateByLocalDateAndLocalTime(endLocalDate,endTime):null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public ShiftDTO(String name, @NotNull(message = "error.ShiftDTO.startDate.notEmpty") Date startDate, @NotNull(message = "error.ShiftDTO.endDate.notnull") Date endDate, long bid, long pId, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, @Range(min = 0) @NotNull(message = "error.ShiftDTO.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, List<ShiftDTO> subShifts) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = bid;
        this.pId = pId;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.subShifts = subShifts;
    }

    public ShiftDTO() {
        //default Const
    }


    public Shift buildShift() {

        Shift shift = new Shift(this.id, this.name, DateUtils.getDateByLocalDateAndLocalTime(this.startLocalDate,this.startTime), DateUtils.getDateByLocalDateAndLocalTime(this.endLocalDate,this.endTime), this.bid, this.pId, this.bonusTimeBank, this.amount, this.probability, this.accumulatedTimeBankInMinutes, this.remarks, this.activityId, this.staffId, this.unitId, this.unitPositionId);
        shift.setDurationMinutes(this.durationMinutes);
        shift.setScheduledMinutes(this.scheduledMinutes);
        shift.setShiftState(ShiftState.UNPUBLISHED);
        return shift;
    }

  /*  public ShiftQueryResult buildResponse() {

        ShiftQueryResult shiftQueryResult = new ShiftQueryResult(this.id, this.name, this.startDate, this.endDate, this.bid, this.pId, this.bonusTimeBank, this.amount, this.probability, this.accumulatedTimeBankInMinutes, this.remarks, this.activityId, this.staffId, this.unitId, this.unitPositionId);
        return shiftQueryResult;
    }*/

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}
