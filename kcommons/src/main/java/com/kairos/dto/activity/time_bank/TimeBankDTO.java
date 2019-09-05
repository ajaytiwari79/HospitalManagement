package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeBankDTO {

    private Long employmentId;
    private Long staffId;
    private int workingDaysInWeek;
    private int totalWeeklyMin;
    private Date startDate;
    private Date endDate;
    private Long unitId;
    private String query;

    private long totalTimeBankAfterCtaMin;
    private long totalTimeBankBeforeCtaMin;
    private long totalScheduledMin;
    private long totalTimeBankMin;
    private long totalContractedMin;
    private long totalTimeBankMinLimit;
    private long totalTimeBankMaxLimit;
    private long totalTimeBankInPercent = 10;
    private long totalTimeBankDiff;
    private long paidoutChange;
    private long approvePayOut;
    private long requestPayOut;
    private long totalDeltaBalanceCorrection;
    private String phaseName;

    //Distributed min on the basis of Interval;
    private List<TimeBankIntervalDTO> timeIntervals = new ArrayList<>();
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private EmploymentWithCtaDetailsDTO costTimeAgreement;

    private List<TimeBankIntervalDTO> weeklyIntervalsTimeBank;
    private List<TimeBankIntervalDTO> monthlyIntervalsTimeBank;
    private float hourlyCost;
    private long totalPlannedMinutes;
    private long actualTimebankMinutes;
    private LocalDate planningPeriodStartDate;
    private LocalDate planningPeriodEndDate;

    public TimeBankDTO(Long employmentId, Long staffId, int workingDaysInWeek, int totalWeeklyMins)
     {
        this.employmentId = employmentId;
        this.staffId = staffId;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMin = totalWeeklyMins;
    }


    public float getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(float hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public long getTotalDeltaBalanceCorrection() {
        return totalDeltaBalanceCorrection;
    }

    public void setTotalDeltaBalanceCorrection(long totalDeltaBalanceCorrection) {
        this.totalDeltaBalanceCorrection = totalDeltaBalanceCorrection;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public TimeBankDTO() {
        this.phaseName = "Total";
    }

    public TimeBankDTO(Date startDate, Date endDate, EmploymentWithCtaDetailsDTO costTimeAgreement, Long staffId, Long employmentId, int totalWeeklyMin, int workingDaysInWeek) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.costTimeAgreement = costTimeAgreement;
        this.phaseName = "Total";
        this.totalWeeklyMin = totalWeeklyMin;
        this.workingDaysInWeek = workingDaysInWeek;
    }


}
