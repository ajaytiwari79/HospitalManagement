package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private double totalTimeBankAfterCtaMin;
    private double totalTimeBankBeforeCtaMin;
    private double totalScheduledMin;
    private double totalTimeBankMin;
    private double totalContractedMin;
    private double totalTimeBankMinLimit;
    private double totalTimeBankMaxLimit;
    private double totalTimeBankInPercent = 10;
    private double totalTimeBankDiff;
    private double paidoutChange;
    private double approvePayOut;
    private double requestPayOut;
    private double totalDeltaBalanceCorrection;
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
    private double totalPlannedMinutes;
    private double actualTimebankMinutes;
    private LocalDate planningPeriodStartDate;
    private LocalDate planningPeriodEndDate;
    private double totalContractedCost;
    private double totalPlannedCost;
    private double totalTimeBankDiffCost;
    private double timeBankOffMinutes;
    private double protectedDaysOffMinutes;



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
