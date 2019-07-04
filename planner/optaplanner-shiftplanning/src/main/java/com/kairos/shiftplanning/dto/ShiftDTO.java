package com.kairos.shiftplanning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.*;


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
    private BigInteger activityId;
    private Long unitId;
    private Long staffId;
    private Long unitEmploymentPositionId;

    private List<ShiftDTO> subShifts = new ArrayList<>();


    public ShiftDTO() {
        //default Const
    }


    public ShiftDTO(String name, Date startDate, Date endDate, BigInteger activityId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = 0l;
        this.pId = 0l;
        this.bonusTimeBank = 0l;
        this.amount = 0l;
        this.probability = 0l;
        this.accumulatedTimeBankInMinutes = 0l;
        this.remarks = "";
        this.activityId = activityId;
        this.unitId = 95l;
        this.staffId = 1037l;
    }

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public void setUnitEmploymentPositionId(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public ShiftDTO(Date startDate, Date endDate, BigInteger activityId, Long unitId, Long staffId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
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

    public List<ShiftDTO> getSubShifts() {
        return subShifts;
    }

    public void setSubShifts(List<ShiftDTO> subShifts) {
        this.subShifts = subShifts;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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
                ", subShifts=" + subShifts +
                '}';
    }
}

