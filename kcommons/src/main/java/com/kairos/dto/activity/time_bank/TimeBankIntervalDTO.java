package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class TimeBankIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalTimeBankAfterCtaMin;
    private long totalTimeBankBeforeCtaMin;
    //Its is the sum of scheduled minutes of payout and timebank
    private long totalScheduledMin;
    private long totalTimeBankMin;
    private long totalContractedMin;
    private long totalTimeBankDiff;
    private long paidoutChange;
    private long approvePayOut;
    private long requestPayOut;
    private long totalDeltaBalanceCorrection;
    private String phaseName;
    private String title;
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private String headerName;
    //Its is the sum of Planned minutes of payout and timebank
    private long totalPlannedMinutes;
    private int timeBankChangeMinutes;
    private long accumulatedTimebankMinutes;
    private long expectedTimebankMinutes;
    private long publishedBalancesMinutes;
    private float totalContractedCost;
    private float totalPlannedCost;
    private float totalTimeBankDiffCost;
    private long timeBankOffMinutes;
    private long protectedDaysOffMinutes;

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
