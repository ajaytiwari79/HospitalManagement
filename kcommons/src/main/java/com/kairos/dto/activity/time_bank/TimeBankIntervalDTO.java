package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class TimeBankIntervalDTO implements Serializable {

    private Date startDate;
    private Date endDate;
    //In minutes
    private double totalTimeBankAfterCtaMin;
    private double totalTimeBankBeforeCtaMin;
    //Its is the sum of scheduled minutes of payout and timebank
    private double totalScheduledMin;
    private double totalTimeBankMin;
    private double totalContractedMin;
    private double totalTimeBankDiff;
    private double paidoutChange;
    private double approvePayOut;
    private double requestPayOut;
    private double totalDeltaBalanceCorrection;
    private String phaseName;
    private String title;
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private String headerName;
    //Its is the sum of Planned minutes of payout and timebank
    private double totalPlannedMinutes;
    private double timeBankChangeMinutes;
    private double accumulatedTimebankMinutes;
    private double expectedTimebankMinutes;
    private double publishedBalancesMinutes;
    private double totalContractedCost;
    private double totalPlannedCost;
    private double totalTimeBankDiffCost;
    private double timeBankOffMinutes;
    private double protectedDaysOffMinutes;
    private int sequence;

    public TimeBankIntervalDTO(String title) {
        this.title = title;
    }

    public TimeBankIntervalDTO(int timeBankChangeMinutes, long accumulatedTimebankMinutes, long expectedTimebankMinutes, long publishedBalancesMinutes,TimeBankCTADistributionDTO timeBankDistribution) {
        this.timeBankChangeMinutes = timeBankChangeMinutes;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
        this.expectedTimebankMinutes = expectedTimebankMinutes;
        this.publishedBalancesMinutes = publishedBalancesMinutes;
        this.timeBankDistribution = timeBankDistribution;
    }

    public TimeBankIntervalDTO(long totalScheduledMin, long totalTimeBankDiff, String title) {
        this.totalScheduledMin = totalScheduledMin;
        this.totalTimeBankDiff = totalTimeBankDiff;
        this.title = title;
    }

    public TimeBankIntervalDTO(Date startDate, Date endDate,String phaseName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.phaseName = phaseName;
    }
}
