package com.kairos.shiftplanning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

